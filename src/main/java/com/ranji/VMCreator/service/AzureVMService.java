package com.ranji.VMCreator.service;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.KnownWindowsVirtualMachineImage;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.ranji.VMCreator.model.Vm;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AzureVMService {

    private static final Logger log = LoggerFactory.getLogger(AzureVMService.class);

    @Value("${AZURE_TENANT_ID}")
    String AZURE_TENANT_ID;
    @Value("${AZURE_CLIENT_ID}")
    String AZURE_CLIENT_ID;
    @Value("${AZURE_CLIENT_SECRET}")
    String AZURE_CLIENT_SECRET;
    @Value("${AZURE_SUBSCRIPTION_ID}")
    String AZURE_SUBSCRIPTION_ID;

    private static final long DESTROY_DELAY = 10 * 60 * 1000; // 10 minutes

    private String vmName = "";
    private String vmUser = "";
    private String sshKey = "";
    private String resourceGroup = "";
    private String chosenVm = "";
    private VirtualMachine createdVM;
    private AzureResourceManager azureResourceManager;

    public String createAndDestroyVM(Vm vm){
        System.setProperty("AZURE_TENANT_ID", AZURE_TENANT_ID);
        System.setProperty("AZURE_CLIENT_ID", AZURE_CLIENT_ID);
        System.setProperty("AZURE_CLIENT_SECRET", AZURE_CLIENT_SECRET);
        System.setProperty("AZURE_SUBSCRIPTION_ID", AZURE_SUBSCRIPTION_ID);

        this.vmUser = vm.getVmUser();
        this.sshKey = vm.getSshKey();
        this.vmName = vm.getVmName();
        this.resourceGroup = vm.getResourceGroup();
        this.chosenVm = vm.getChosenVm();

        TokenCredential credential = new EnvironmentCredentialBuilder()
                .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
                .build();

        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        this.azureResourceManager = AzureResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withDefaultSubscription();

        String ipAddress = createVirtualMachine();

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(DESTROY_DELAY);
                destroyVirtualMachine();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return ipAddress;
    }

    public String createVirtualMachine() {
        try {
            if(this.chosenVm == null || this.chosenVm.isEmpty() ){
                this.chosenVm = "DEBIAN_9";
            }

            this.setMachine(this.chosenVm);

            String publicIPAddress = this.createdVM.getPrimaryPublicIPAddressId();
            if (publicIPAddress != null) {
                PublicIpAddress publicIP = this.azureResourceManager.publicIpAddresses().getById(publicIPAddress);
                log.debug("Adresse IP publique : " + publicIP.ipAddress());
                return publicIP.ipAddress();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            this.azureResourceManager.resourceGroups().deleteByName(this.resourceGroup);
            log.debug("Adresse IP publique non trouvée pour la machine virtuelle.");
            return null;
        }
        return null;
    }

    private void destroyVirtualMachine() {
        try{
            azureResourceManager.resourceGroups().deleteByName(this.resourceGroup);
            log.debug("Groupe de ressources {} supprimé avec succès.", this.resourceGroup);
        }
        catch (Exception e){
            log.debug("Erreur lors de la suppression du groupe de ressources. ");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getVmList(){
        KnownLinuxVirtualMachineImage[] values = KnownLinuxVirtualMachineImage.values();
        List<String> linuxImages = Arrays.stream(values)
                .map(Enum::name).toList();
        KnownWindowsVirtualMachineImage[] values2 = KnownWindowsVirtualMachineImage.values();
        List<String> winImages = Arrays.stream(values2)
                .map(Enum::name).toList();
        List<String> mergedList = new ArrayList<>(linuxImages);
        mergedList.addAll(winImages);
        return mergedList;
    }

    public boolean isLinuxImage(String vmChoice){
        try{
            KnownLinuxVirtualMachineImage vmImage = KnownLinuxVirtualMachineImage.valueOf(vmChoice);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public void setMachine(String vmChoice){
        log.debug("Image de la vm choisie : " + vmChoice);
        if(this.isLinuxImage(vmChoice)){
            KnownLinuxVirtualMachineImage vmImage = KnownLinuxVirtualMachineImage.valueOf(vmChoice);
            this.createdVM = this.azureResourceManager.virtualMachines().define(this.vmName)
                    .withRegion(Region.FRANCE_CENTRAL)
                    .withNewResourceGroup(this.resourceGroup)
                    .withNewPrimaryNetwork("10.0.0.0/24")
                    .withPrimaryPrivateIPAddressDynamic()
                    .withNewPrimaryPublicIPAddress(this.resourceGroup)
                    .withPopularLinuxImage(vmImage)
                    .withRootUsername(this.vmUser)
                    .withSsh(this.sshKey)
                    .withSize(VirtualMachineSizeTypes.STANDARD_D3_V2)
                    .create();
        }
        else {
            KnownWindowsVirtualMachineImage vmImage = KnownWindowsVirtualMachineImage.valueOf(vmChoice);
            this.createdVM = this.azureResourceManager.virtualMachines().define(this.vmName)
                    .withRegion(Region.FRANCE_CENTRAL)
                    .withNewResourceGroup(this.resourceGroup)
                    .withNewPrimaryNetwork("10.0.0.0/24")
                    .withPrimaryPrivateIPAddressDynamic()
                    .withNewPrimaryPublicIPAddress(this.resourceGroup)
                    .withPopularWindowsImage(vmImage)
                    .withAdminUsername(this.vmUser)
                    .withAdminPassword(this.sshKey)
                    .withSize(VirtualMachineSizeTypes.STANDARD_D3_V2)
                    .create();
        }
    }

    @PreDestroy
    public void cleanup() {
        if (this.resourceGroup != null && !this.resourceGroup.isEmpty()) {
            try {
                this.destroyVirtualMachine();
            } catch (Exception e) {
                log.error("Erreur lors de la suppression du groupe de ressources.", e);
            }
        }
    }


}
