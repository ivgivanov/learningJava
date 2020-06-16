import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.Permission;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
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

            //ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

            // Get the reference to the root folder
            ManagedObjectReference rootFolder = serviceContent.getRootFolder();
            ManagedObjectReference vmFolder = new ManagedObjectReference();
            vmFolder.setType("Folder");
            vmFolder.setValue("group-v90");
            collectProperties(vimPort, serviceContent, vmFolder);

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

            //Create role with privileges, get role, update role, assign role
            /*
            
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

            AccessControlManager accessControl = new AccessControlManager();
            
            //int newRoleId =  accessControl.createRole(vimPort, serviceContent, roleName, privileges);

            int roleId = accessControl.getRoleId(vimPort, serviceContent, roleName);
            System.out.println(roleId);
            //accessControl.updateRole(vimPort, serviceContent, roleId, roleName, privileges);

            ManagedObjectReference myVM = new ManagedObjectReference();
            myVM.setType("VirtualMachine");
            myVM.setValue("vm-89");

            List<Permission> permissions = new ArrayList<Permission>();
            permissions.add(accessControl.buildPermission(roleId, "Domain\\Service users", true, true));
            permissions.add(accessControl.buildPermission(roleId, "local_user", false, true));

            vimPort.setEntityPermissions(serviceContent.getAuthorizationManager(), myVM, permissions);

            */

            

            vimPort.logout(serviceContent.getSessionManager());

        } catch (RuntimeFaultFaultMsg e) {
            e.printStackTrace();
        } catch (InvalidLocaleFaultMsg e) {
            e.printStackTrace();
        } catch (InvalidLoginFaultMsg e) {
            e.printStackTrace();
        }

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
        objectSpec.getSelectSet().addAll(buildFullTraversal());
        objectSpec.setSkip(false);

        PropertySpec propertySpecVM = new PropertySpec();
        propertySpecVM.setType("VirtualMachine");
        propertySpecVM.getPathSet().add("name");
        propertySpecVM.setAll(false);

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpecVM);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();

        RetrieveResult result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList, retrieveOptions);

        if (result != null) {
            System.out.println("All VMs under: "+moid.getValue());
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

    public static List<SelectionSpec> buildFullTraversal() {
        // Terminal traversal specs

        // RP -> VM
        TraversalSpec rpToVm = new TraversalSpec();
        rpToVm.setName("rpToVm");
        rpToVm.setType("ResourcePool");
        rpToVm.setPath("vm");
        rpToVm.setSkip(Boolean.FALSE);

        // vApp -> VM
        TraversalSpec vAppToVM = new TraversalSpec();
        vAppToVM.setName("vAppToVM");
        vAppToVM.setType("VirtualApp");
        vAppToVM.setPath("vm");

        // HostSystem -> VM
        TraversalSpec hToVm = new TraversalSpec();
        hToVm.setType("HostSystem");
        hToVm.setPath("vm");
        hToVm.setName("hToVm");
        hToVm.getSelectSet().add(getSelectionSpec("VisitFolders"));
        hToVm.setSkip(Boolean.FALSE);

        // DC -> DS
        TraversalSpec dcToDs = new TraversalSpec();
        dcToDs.setType("Datacenter");
        dcToDs.setPath("datastore");
        dcToDs.setName("dcToDs");
        dcToDs.setSkip(Boolean.FALSE);

        // Recurse through all ResourcePools
        TraversalSpec rpToRp = new TraversalSpec();
        rpToRp.setType("ResourcePool");
        rpToRp.setPath("resourcePool");
        rpToRp.setSkip(Boolean.FALSE);
        rpToRp.setName("rpToRp");
        rpToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

        TraversalSpec crToRp = new TraversalSpec();
        crToRp.setType("ComputeResource");
        crToRp.setPath("resourcePool");
        crToRp.setSkip(Boolean.FALSE);
        crToRp.setName("crToRp");
        crToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

        TraversalSpec crToH = new TraversalSpec();
        crToH.setSkip(Boolean.FALSE);
        crToH.setType("ComputeResource");
        crToH.setPath("host");
        crToH.setName("crToH");

        TraversalSpec dcToHf = new TraversalSpec();
        dcToHf.setSkip(Boolean.FALSE);
        dcToHf.setType("Datacenter");
        dcToHf.setPath("hostFolder");
        dcToHf.setName("dcToHf");
        dcToHf.getSelectSet().add(getSelectionSpec("VisitFolders"));

        TraversalSpec vAppToRp = new TraversalSpec();
        vAppToRp.setName("vAppToRp");
        vAppToRp.setType("VirtualApp");
        vAppToRp.setPath("resourcePool");
        vAppToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

        TraversalSpec dcToVmf = new TraversalSpec();
        dcToVmf.setType("Datacenter");
        dcToVmf.setSkip(Boolean.FALSE);
        dcToVmf.setPath("vmFolder");
        dcToVmf.setName("dcToVmf");
        dcToVmf.getSelectSet().add(getSelectionSpec("VisitFolders"));

        // For Folder -> Folder recursion
        TraversalSpec visitFolders = new TraversalSpec();
        visitFolders.setType("Folder");
        visitFolders.setPath("childEntity");
        visitFolders.setSkip(Boolean.FALSE);
        visitFolders.setName("VisitFolders");
        List<SelectionSpec> sspecarrvf = new ArrayList<SelectionSpec>();
        sspecarrvf.add(getSelectionSpec("crToRp"));
        sspecarrvf.add(getSelectionSpec("crToH"));
        sspecarrvf.add(getSelectionSpec("dcToVmf"));
        sspecarrvf.add(getSelectionSpec("dcToHf"));
        sspecarrvf.add(getSelectionSpec("vAppToRp"));
        sspecarrvf.add(getSelectionSpec("vAppToVM"));
        sspecarrvf.add(getSelectionSpec("dcToDs"));
        sspecarrvf.add(getSelectionSpec("hToVm"));
        sspecarrvf.add(getSelectionSpec("rpToVm"));
        sspecarrvf.add(getSelectionSpec("VisitFolders"));

        visitFolders.getSelectSet().addAll(sspecarrvf);

        List<SelectionSpec> resultspec = new ArrayList<SelectionSpec>();
        resultspec.add(visitFolders);
        resultspec.add(crToRp);
        resultspec.add(crToH);
        resultspec.add(dcToVmf);
        resultspec.add(dcToHf);
        resultspec.add(vAppToRp);
        resultspec.add(vAppToVM);
        resultspec.add(dcToDs);
        resultspec.add(hToVm);
        resultspec.add(rpToVm);
        resultspec.add(rpToRp);

        return resultspec;
    }

    public static SelectionSpec getSelectionSpec(String name) {
        SelectionSpec genericSpec = new SelectionSpec();
        genericSpec.setName(name);
        return genericSpec;
    }

}
