/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.app;


import java.util.HashMap;
import java.util.Map;

import lixl.concurrent.runner.RunnerConfigParameters;
import lixl.concurrent.runner.RunnerControl;
import lixl.workshop.cassandra.app.runner.UserInsertionRunnable;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.ConfigurationFactory.PropertiesConfigurationFactory;
import org.apache.commons.pool.impl.GenericObjectPool;


import lixl.workshop.cassandra.client.da.SimpleDao;
import lixl.workshop.cassandra.client.da.SimpleDaoPools;
import lixl.workshop.cassandra.client.vo.EncodedVO;
import lixl.workshop.cassandra.client.vo.VOFactoryBase;
import lixl.workshop.cassandra.client.vo.VOFactoryException;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
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

		SimpleDaoPools l_daoPools = new SimpleDaoPools();
		l_daoPools.setConfiguration(CFG);
		
		final GenericObjectPool<SimpleDao> l_daoPool = l_daoPools.getSimpleDaoPool();
		
		l_daoPool.setMaxActive(CFG.getInt(RunnerConfigParameters.MAX_INJECT_THREAD_COUNT));
		
//		l_runCtr.getReportAssembler().appendReport(new StringReport() {
//			
//			@Override
//			public String getStringReport() {
//				return String.format("%1d,%2d,%3d", 
//						l_daoPool.getNumActive(),
//						l_daoPool.getNumIdle(),
//						l_daoPool.getMaxActive()
//						);
//			}
//
//			@Override
//			public String getHeaders() {
//				return "ActiveDaoNum,IdleDaoNum,MaxActDaoNum";
//			}
//		});
		
		l_runCtr.start();
		
		for (int l_i = 0; l_i < CFG.getLong(RunnerConfigParameters.TASK_COUNT); l_i++) {
			Map<String, Object> l_voRawCols = new HashMap<String, Object>();
			
			l_voRawCols.put("address", "Aya raja crescent");
			l_voRawCols.put("age", l_i);
			EncodedVO l_userVo = l_voFac.newVO(Double.toString(Math.random()), l_voRawCols);
			UserInsertionRunnable l_insertTask = new UserInsertionRunnable();
			
			l_insertTask.setDaoPool(l_daoPool);
			l_insertTask.setValueObject(l_userVo);
			
			l_runCtr.submit(l_insertTask);
		}
		
		l_runCtr.notifyShutdown();
	}

}
