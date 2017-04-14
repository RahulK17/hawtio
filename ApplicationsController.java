package io.hawt.sample.spring.boot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationsController {

	@Autowired
	ApplicationsDao dao;

	@Autowired
	RestHelper helper;

	@Autowired
	ApplicationServer applicationServer;

	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationsController.class);

	@RequestMapping("/hawtio/custom/applications-list")
	public @ResponseBody List<Application> getApplications() {
		return dao.listApplications();
	}

	@RequestMapping("/hawtio/custom/applications-status")
	public @ResponseBody List<ApplicationStatus> getApplicationsStatus() {
		List<Application> apps = dao.listApplications();
		List<ApplicationStatus> statuses = new ArrayList<ApplicationStatus>();
		for (Application app : apps) {
			ApplicationStatus status = new ApplicationStatus();
			status.application = app; // for connect option in ui
			status.name = app.name;
			status.cpu = String.valueOf((int) (helper.getCpuUsage(app) * 100));
			status.heap = String.valueOf((int) (helper.getHeapMemoryUsage(app) * 100));
			statuses.add(status);
		}
		return statuses;
	}

	@RequestMapping("/hawtio/custom/alert-status")
	public @ResponseBody List<Alert> getAlerts() {
		List<Alert> alertsList = dao.listAlerts();
		return alertsList;
	}

	/**
	 * This Application server REST endpoint will connect to SSH server. 
	 */
	@RequestMapping("/hawtio/custom/start-application")
	public @ResponseBody void doApplicationStart() {

		LOGGER.info("start apllication server is invoking");
		applicationServer.start();

		
	}
}
