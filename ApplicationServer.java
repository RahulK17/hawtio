package io.hawt.sample.spring.boot;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author rahulkarre
 *
 *  This class will connect to SSH server and execute commands.  
 */
@Component
public class ApplicationServer {

	@Value(value = "${application.server.host}")
	private String host;
	@Value(value = "${application.server.user}")
	private String user;
	@Value(value = "${application.server.password}")
	private String password;
	@Value(value = "${application.server.port}")
	private int port;
	@Value(value = "${application.server.command}")
	private String command;

	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServer.class);

	/*
	 * public void checkProps(){ System.out.println("host"+host);
	 * LOGGER.info("Connected to Application Host::"+host);
	 * System.out.println("user"+user); System.out.println("password"+password);
	 * System.out.println("port"+port); System.out.println("command"+command);
	 * 
	 * }
	 */
	/**
	 * This method will get the SSH server session and execute commands to the same server
	 */
	public void start() {
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			LOGGER.info("Connected to Application server host::" + host + ": and user::" + user);
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					
					if (i < 0)
						break;
					LOGGER.info(new String(tmp, 0, i));
					/* System.out.print(new String(tmp, 0, i)); */
				}
				if (channel.isClosed()) {
					LOGGER.info("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			LOGGER.info("Succesfully disconnected application server::" + host + "and user::" + user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
