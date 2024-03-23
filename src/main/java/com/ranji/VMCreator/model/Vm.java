package com.ranji.VMCreator.model;

public class Vm {
    private String sshKey;
    private String vmName;

    private String resourceGroup;

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

    @Override
    public String toString() {
        return "Vm{" +
                "sshKey='" + sshKey + '\'' +
                ", vmName='" + vmName + '\'' +
                ", resourceGroup='" + resourceGroup + '\'' +
                '}';
    }
}
