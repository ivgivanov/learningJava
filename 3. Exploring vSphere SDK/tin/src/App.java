import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

import com.vmware.vim25.AlreadyExistsFaultMsg;
import com.vmware.vim25.AuthorizationRole;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.ServiceManagerServiceInfo;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualMachineConfigInfo;

public class App {
    public static void main(String[] args) throws Exception {

        String url = "";
        String user = "";
        String password = "";

        if (args.length == 3) {
            url = "https://" + args[0] + "/sdk/vimService";
            user = args[1];
            password = args[2];
        } else {
            App readProps = new App();
            Properties confProps = readProps.getPropValue("config.properties");
            url = confProps.getProperty("url");
            user = confProps.getProperty("user");
            password = confProps.getProperty("password");
        }

        VimService vimService = new VimService();
        VimPortType vimPort = vimService.getVimPort();

        Map<String, Object> ctxt = ((BindingProvider) vimPort).getRequestContext();
        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

        // Disable all SSL trust security
        SslManager.trustEveryone();

        ManagedObjectReference serviceInstance = new ManagedObjectReference();
        serviceInstance.setType("ServiceInstance");
        serviceInstance.setValue("ServiceInstance");

        try {
            ServiceContent serviceContent = vimPort.retrieveServiceContent(serviceInstance);
            vimPort.login(serviceContent.getSessionManager(), user, password, null);

            // print basic info
            // System.out.println(serviceContent.getAbout().getLicenseProductName());
            // System.out.println("Version: " + serviceContent.getAbout().getVersion());
            // System.out.println("Build: " + serviceContent.getAbout().getBuild());

            // using property collector

            ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

            // Get the reference to the root folder
            // ManagedObjectReference rootFolder = serviceContent.getRootFolder();
            // collectProperties(vimPort, serviceContent, rootFolder);

            // VM info
            /*
             * ManagedObjectReference vm = new ManagedObjectReference();
             * vm.setType("VirtualMachine"); vm.setValue("vm-89"); VirtualMachineConfigInfo
             * vmConfig = getVmConfig(vimPort, propertyCollector, vm);
             * System.out.println("VM name: "+vmConfig.getName());
             * System.out.println("VM hw version: "+vmConfig.getVersion());
             * List<VirtualDevice> vmDevices = vmConfig.getHardware().getDevice(); for
             * (VirtualDevice vmDevice : vmDevices) { if
             * (Class.forName("com.vmware.vim25.VirtualEthernetCard").isAssignableFrom(
             * vmDevice.getClass())) { VirtualEthernetCard vmEthCard =
             * (VirtualEthernetCard)vmDevice;
             * System.out.println(vmDevice.getDeviceInfo().getLabel()+", MAC: "+vmEthCard.
             * getMacAddress()); } }
             */

            /*ManagedObjectReference obj = vimPort.findByInventoryPath(serviceContent.getSearchIndex(),
                    "Bellatrix/host/BN/bellatrix-esxi67-5.solar.system");
            System.out.println(obj.getType());*/

            //Create role with privileges 
            String roleName = "ServiceAccounts";
            List<String> privileges = new ArrayList<String>();
            privileges.add("Global.Settings");
            privileges.add("Host.Cim.CimInteraction");
            privileges.add("Host.Config.AdvancedConfig");
            privileges.add("Host.Config.Firmware");
            privileges.add("Host.Config.NetService");
            privileges.add("Host.Config.Settings");
            privileges.add("VirtualMachine.Config.AdvancedConfig");
            privileges.add("Extension.Register");
            privileges.add("Extension.Unregister");
            privileges.add("Extension.Update");

            int roleId = createRole(vimPort, serviceContent, roleName, privileges);

            vimPort.logout(serviceContent.getSessionManager());

        } catch (RuntimeFaultFaultMsg e) {
            e.printStackTrace();
        } catch (InvalidLocaleFaultMsg e) {
            e.printStackTrace();
        } catch (InvalidLoginFaultMsg e) {
            e.printStackTrace();
        }

    }

    public static int createRole(VimPortType vimPort, ServiceContent serviceContent, String name, List<String> privs) {
        
        int roleId = 0;
        try {
            roleId = vimPort.addAuthorizationRole(serviceContent.getAuthorizationManager(), name, privs);
            System.out.println("'"+name+"'"+" role created! Privileges assinged:");
            for (String privilege : privs ) {
                System.out.println(privilege);
            }
            return roleId;
        } catch (AlreadyExistsFaultMsg e) {
            System.out.println("Role with this name already exists");
            //e.printStackTrace();
        } catch (InvalidNameFaultMsg e) {
            System.out.println("Invalid role name");
            //e.printStackTrace();
        } catch (RuntimeFaultFaultMsg e) {
            System.out.println("An error occured");
            //e.printStackTrace();
        }
        return roleId;

    }

    private static VirtualMachineConfigInfo getVmConfig(VimPortType vimPort, ManagedObjectReference propertyCollector, ManagedObjectReference moid)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(moid);

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setType(moid.getType());
        propertySpec.getPathSet().add("config");

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpec);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();
        RetrieveResult result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList, retrieveOptions);

        if (result != null) {

            for (ObjectContent objectContent : result.getObjects()) {
                List<DynamicProperty> properties = objectContent.getPropSet();
                if (properties.size() == 1) {
                    VirtualMachineConfigInfo vmConfig = (VirtualMachineConfigInfo)properties.get(0).getVal();   
                    return vmConfig;
                } else {
                    return null;
                }
            }
        } 

        return null;
    }

    private static void collectProperties(VimPortType vimPort, ServiceContent serviceContent, ManagedObjectReference moid)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        // Get the reference to the PropertyCollector
        ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(moid);

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setType(moid.getType());
        propertySpec.setAll(true);

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpec);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();

        RetrieveResult result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList, retrieveOptions);

        if (result != null) {
            for (ObjectContent objectContent : result.getObjects()) {
                List<DynamicProperty> properties = objectContent.getPropSet();
                for (DynamicProperty property : properties) {
                    System.out.println(property.getName() + ": " + property.getVal());
                }
            }
        }

    }

    public Properties getPropValue(String fileName) throws IOException {

        Properties prop = new Properties();
        prop.load(getClass().getClassLoader().getResourceAsStream(fileName));
        return prop;

    }

}
