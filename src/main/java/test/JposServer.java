package test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ServerChannel;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

public class JposServer implements ISORequestListener {
	 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ISOException {
        String hostname = "localhost";
        int portNumber = 12345;
 
        // membuat sebuah packager
        ISOPackager packager = new GenericPackager("packager/iso93ascii.xml");
        // membuat channel
        ServerChannel channel = new ASCIIChannel(hostname, portNumber, packager);
        // membuat server
        ISOServer server = new ISOServer(portNumber, channel, null);
        server.addISORequestListener(new JposServer());
 
        new Thread(server).start();
 
        System.out.println("Server siap menerima koneksi pada port [" + portNumber+"]");
    }
 
    public boolean process(ISOSource isoSrc, ISOMsg isoMsg) {
        try {
            System.out.println("Server menerima koneksi dari ["+((BaseChannel)isoSrc).getSocket().getInetAddress().getHostAddress()+"]");
            if (isoMsg.getMTI().equalsIgnoreCase("1800")) {
                    acceptNetworkMsg(isoSrc, isoMsg);
            }
        } catch (IOException ex) {
            Logger.getLogger(JposServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ISOException ex) {
            Logger.getLogger(JposServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
 
    private void acceptNetworkMsg(ISOSource isoSrc, ISOMsg isoMsg) throws ISOException, IOException {
        System.out.println("Accepting Network Management Request");
        ISOMsg reply = (ISOMsg) isoMsg.clone();
        reply.setMTI("1810");
        reply.set(39, "00");
 
        isoSrc.send(reply);
    }
}
