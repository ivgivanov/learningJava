import java.util.ArrayList;
import java.util.List;

import com.vmware.vim25.AlreadyExistsFaultMsg;
import com.vmware.vim25.ArrayOfAuthorizationRole;
import com.vmware.vim25.AuthorizationRole;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NotFoundFaultMsg;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.Permission;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

public class AccessControlManager {

    public int createRole(VimPortType vimPort, ServiceContent serviceContent, String name, List<String> privs) {

        int roleId = 0;
        try {
            roleId = vimPort.addAuthorizationRole(serviceContent.getAuthorizationManager(), name, privs);
            System.out.println("'" + name + "'" + " role created! Privileges assinged:");
            for (String privilege : privs) {
                System.out.println(privilege);
            }
            return roleId;
        } catch (AlreadyExistsFaultMsg e) {
            System.out.println("Role with this name already exists");
            // e.printStackTrace();
        } catch (InvalidNameFaultMsg e) {
            System.out.println("Invalid role name");
            // e.printStackTrace();
        } catch (RuntimeFaultFaultMsg e) {
            System.out.println("An error occured");
            // e.printStackTrace();
        }
        return roleId;

    }

    public void updateRole(VimPortType vimPort, ServiceContent serviceContent, int roleId, String newName,
            List<String> privs) {

        try {
            vimPort.updateAuthorizationRole(serviceContent.getAuthorizationManager(), roleId, newName, privs);
        } catch (AlreadyExistsFaultMsg e) {
            System.out.println("Role with this name already exists");
            // e.printStackTrace();
        } catch (InvalidNameFaultMsg e) {
            System.out.println("Invalid role name");
            // e.printStackTrace();
        } catch (NotFoundFaultMsg e) {
            System.out.println("Role with ID " + roleId + " not found");
            // e.printStackTrace();
        } catch (RuntimeFaultFaultMsg e) {
            System.out.println("An error occured");
            // e.printStackTrace();
        }

    }

    public int getRoleId(VimPortType vimPort, ServiceContent serviceContent, String name)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        ManagedObjectReference authManager = serviceContent.getAuthorizationManager();
        ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(authManager);

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setType(authManager.getType());
        propertySpec.getPathSet().add("roleList");

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpec);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();
        RetrieveResult result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList,
                retrieveOptions);

        if (result != null) {

            for (ObjectContent objectContent : result.getObjects()) {
                List<DynamicProperty> properties = objectContent.getPropSet();
                if (properties.size() == 1) {
                    ArrayOfAuthorizationRole authRolesArray = (ArrayOfAuthorizationRole) properties.get(0).getVal();
                    List<AuthorizationRole> authRoles = authRolesArray.getAuthorizationRole();
                    for (AuthorizationRole role : authRoles) {
                        if (role.getName().equals(name)) {
                            return role.getRoleId();
                        }
                    }
                } else {
                    return 0;
                }
            }
        }

        return 0;
    }

    public Permission buildPermission(int roleId, String principal, boolean group, boolean propagate) {
        Permission permission = new Permission();

        permission.setEntity(null);
        permission.setRoleId(roleId);
        permission.setPrincipal(principal);
        permission.setGroup(group);
        permission.setPropagate(propagate);

        return permission;

    }
    
}