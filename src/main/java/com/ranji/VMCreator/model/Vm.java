package com.ranji.VMCreator.model;

public class Vm {
    private String sshKey;
    private String vmName;
    private String vmUser;
    private String resourceGroup;
    private String chosenVm;


    public String getVmUser() {
        return vmUser;
    }

    public void setVmUser(String vmUser) {
        this.vmUser = vmUser;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
    }

    public String getChosenVm() {
        return chosenVm;
    }

    public void setChosenVm(String chosenVm) {
        this.chosenVm = chosenVm;
    }

    @Override
    public String toString() {
        return "Vm{" +
                "sshKey='" + sshKey + '\'' +
                ", vmName='" + vmName + '\'' +
                ", vmUser='" + vmUser + '\'' +
                ", resourceGroup='" + resourceGroup + '\'' +
                ", chosenVm='" + chosenVm + '\'' +
                '}';
    }
}
