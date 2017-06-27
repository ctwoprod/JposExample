package test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequest;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

public class JposClient {
	public static void main(String[] args) throws IOException, ISOException {
		String hostname = "localhost";
		int portNumber = 12345;

		ISOPackager packager = new GenericPackager("packager/iso93ascii.xml");
		ASCIIChannel channel = new ASCIIChannel(hostname, portNumber, packager);

		ISOMUX isoMux = new ISOMUX(channel) {
			@Override
			protected String getKey(ISOMsg m) throws ISOException {
				return super.getKey(m);
			}
		};

		new Thread(isoMux).start();

		ISOMsg networkReq = new ISOMsg();
		networkReq.setMTI("1800");
		networkReq.set(3, "123456");
		networkReq.set(7, new SimpleDateFormat("yyyyMMdd").format(new Date()));
		networkReq.set(11, "000001");
		networkReq.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
		networkReq.set(13, new SimpleDateFormat("MMdd").format(new Date()));
		networkReq.set(48, "Tutorial ISO 8583 Dengan Java");
		networkReq.set(70, "001");

		ISORequest req = new ISORequest(networkReq);
		isoMux.queue(req);

		ISOMsg reply = req.getResponse(50 * 1000);
		if (reply != null) {
			System.out.println("Req [" + new String(networkReq.pack()) + "]");
			System.out.println("Res [" + new String(reply.pack()) + "]");
		}
	}
}
