/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.app;


import java.util.HashMap;
import java.util.Map;

import lixl.concurrent.runner.RunnerConfigParameters;
import lixl.concurrent.runner.RunnerControl;
import lixl.workshop.cassandra.app.runner.UserInsertionRunnable;
import lixl.workshop.cassandra.client.simple.EncodedVO;
import lixl.workshop.cassandra.client.simple.SimpleDao;
import lixl.workshop.cassandra.client.simple.VOFactoryBase;
import lixl.workshop.cassandra.client.simple.VOFactoryException;
import lixl.workshop.cassandra.connection.ClientConnectionFactory;
import lixl.workshop.cassandra.connection.ClientConnectionPool;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
public class LoaderMain {
	private static Configuration CFG = null;
	
	private final static Options getOptions(){
		Options l_options = new Options();
		
		l_options.addOption("cfgFile", true, "configuration file path");
		
		return l_options;
	}
	
	private final static void loadConfiguration(CommandLine p_cli) throws ConfigurationException {
		String CFGFile = null;
		if(p_cli.hasOption("cfgFile")) {
			CFGFile = p_cli.getOptionValue("cfgFile");
		} else {
			CFGFile = "conf/app.properties";
		}
		
		CFG = new PropertiesConfiguration(CFGFile);
		
	}
	
	/**
	 * @param args
	 * @throws ParseException 
	 * @throws ConfigurationException 
	 * @throws VOFactoryException 
	 */
	public static void main(String[] args) throws ParseException, ConfigurationException, VOFactoryException {
		CommandLineParser l_cliPaser = new BasicParser();
		CommandLine l_cli = l_cliPaser.parse(getOptions(), args);
		loadConfiguration(l_cli);
		
		VOFactoryBase.loadDefinitions();
		VOFactoryBase l_voFac = VOFactoryBase.getVOFactory("employee");
		
		RunnerControl l_runCtr = new RunnerControl(CFG);

		ClientConnectionFactory l_ccf = new ClientConnectionFactory();
		l_ccf.setConfiguration(CFG);
		final ClientConnectionPool l_daoPool = new ClientConnectionPool(l_ccf);
		l_daoPool.setMaxActive(CFG.getInt(RunnerConfigParameters.MAX_INJECT_THREAD_COUNT));
		
		l_runCtr.start();
		
		for (int l_i = 0; l_i < CFG.getLong(RunnerConfigParameters.TASK_COUNT); l_i++) {
			Map<String, Object> l_voRawCols = new HashMap<String, Object>();
			
			l_voRawCols.put("address", "Aya raja crescent");
			l_voRawCols.put("age", l_i);
			EncodedVO l_userVo = l_voFac.newVO(Double.toString(Math.random()), l_voRawCols);
			UserInsertionRunnable l_insertTask = new UserInsertionRunnable();
			
			SimpleDao l_simpleDao = new SimpleDao();
			l_simpleDao.setClientConnectionPool(l_daoPool);
			l_simpleDao.setColumnFamily("employee");
			l_insertTask.setSimpleDao(l_simpleDao);
			l_insertTask.setValueObject(l_userVo);
			
			l_runCtr.submit(l_insertTask);
		}
		
		l_runCtr.notifyShutdown();
	}

}
