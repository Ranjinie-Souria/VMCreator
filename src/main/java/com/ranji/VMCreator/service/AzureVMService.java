package com.ranji.VMCreator.service;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import com.ranji.VMCreator.controllers.VMController;
import com.ranji.VMCreator.model.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
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

    private static final long DESTROY_DELAY = 5 * 60 * 1000; // 10 minutes

    private String vmName = "";
    private String vmUser = "";
    private String sshKey = "";
    private String resourceGroup = "";
    private VirtualMachine linuxVM;
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
            // Create an Ubuntu virtual machine in a new resource group.
            this.linuxVM = this.azureResourceManager.virtualMachines().define(this.vmName)
                    .withRegion(Region.FRANCE_CENTRAL)
                    .withNewResourceGroup(this.resourceGroup)
                    .withNewPrimaryNetwork("10.0.0.0/24")
                    .withPrimaryPrivateIPAddressDynamic()
                    .withNewPrimaryPublicIPAddress(this.resourceGroup)
                    .withPopularLinuxImage(KnownLinuxVirtualMachineImage.DEBIAN_9)
                    .withRootUsername(this.vmUser)
                    .withSsh(this.sshKey)
                    .withSize(VirtualMachineSizeTypes.STANDARD_D3_V2)
                    .create();
            String publicIPAddress = this.linuxVM.getPrimaryPublicIPAddressId();
            if (publicIPAddress != null) {
                log.debug("Adresse IP publique : " + publicIPAddress);
                return publicIPAddress;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            log.debug("Adresse IP publique non trouvée pour la machine virtuelle.");
            return null;
        }
        return null;
    }

    private void destroyVirtualMachine() {
        this.azureResourceManager.virtualMachines().deleteById(this.linuxVM.vmId());
    }

}
