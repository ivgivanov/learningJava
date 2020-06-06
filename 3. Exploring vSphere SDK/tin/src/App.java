import java.util.Map;

import javax.xml.ws.BindingProvider;

import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;

public class App {
    public static void main(String[] args) throws Exception {

        String url = "https://"+args[0]+"/sdk/vimService";
        String user = args[1];
        String password = args[2];

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
            System.out.println(serviceContent.getAbout().getLicenseProductName());
            System.out.println("Version: " + serviceContent.getAbout().getVersion());
            System.out.println("Build: " + serviceContent.getAbout().getBuild());
        
            vimPort.logout(serviceContent.getSessionManager());
        
        } catch (RuntimeFaultFaultMsg e) {
            e.printStackTrace();
        } catch (InvalidLocaleFaultMsg e) {
            e.printStackTrace();
        } catch (InvalidLoginFaultMsg e) {
            e.printStackTrace();
        }

    }
}
