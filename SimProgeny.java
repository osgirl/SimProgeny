/*
* Christopher Snyder
* 	christopher.snyder@regeneron.com
* Regeneron Genetics Center
* Summer 2016
* SimProgeny.java V1.1 
*/


// Import packages here
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.Process;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;

/*
* Simprodigy will simulate a breeding population of humans. 
*/
public class SimProgeny
{
  public static class config
  {
		public static double birthrate = 0.025;
		public static double deathrate = 0.012;
		public static double marriagerate = 0.0068;
		public static double immigrationrate = 0.01;
		public static double emigrationrate = 0.01;
    public static double divorcerate = 0.0028;
		public static double full_sibling_rate = 0.8;
    public static double internal_migration_rate = 0.01;
    public static double proportion_initially_married = 0.6;
    public static int fertilityStart = 15;
    public static int fertilityEnd = 50;
    public static int burn_in_period = 100;
    public static int simulation_period = 200;

    public static String ascertainment_approach = "random";
    public static double lambda1stDegreeClusteredAscertainment = 0.25;
    public static double lambda2ndDegreeClusteredAscertainment = 0.1;
    public static double ordered_sampling_proportion = 0.05;
		
    public static ArrayList<Double> male_mortality_by_age = new ArrayList<Double>(
			Arrays.asList(0.006569,0.000444,0.000291,0.000226,0.000173,0.000158,0.000147,0.000136,0.000121,0.000104, // 0 - 9
	      		0.000092,0.000097,0.000134,0.000210,0.000317,0.000433,0.000547,0.000672,0.000805,0.000941, //10 - 19
	      		0.001084,0.001219,0.001314,0.001357,0.001362,0.001353,0.001350,0.001353,0.001371,0.001399, //20 - 29
	      		0.001432,0.001464,0.001497,0.001530,0.001568,0.001617,0.001682,0.001759,0.001852,0.001963, //30 - 39
	      		0.002092,0.002246,0.002436,0.002669,0.002942,0.003244,0.003571,0.003926,0.004309,0.004719, //40 -49
	      		0.005156,0.005622,0.006121,0.006656,0.007222,0.007844,0.008493,0.009116,0.009690,0.010253, //50- 59 
	      		0.010872,0.011591,0.012403,0.013325,0.014370,0.015553,0.016878,0.018348,0.019969,0.021766, //60-69
	      		0.023840,0.026162,0.028625,0.031204,0.033997,0.037200,0.040898,0.045040,0.049664,0.054844, //70-79
              		0.060801,0.067509,0.074779,0.082589,0.091135,0.100680,0.111444,0.123571,0.137126,0.152092, //80-89
	      		0.168426,0.186063,0.204925,0.224931,0.245995,0.266884,0.287218,0.306593,0.324599,0.340829, //90- 99
	      		0.357870,0.375764,0.394552,0.414280,0.434993,0.456743,0.479580,0.503559,0.528737,0.555174,//100-109
	      		0.582933,0.612080,0.642683,0.674818,0.708559,0.743986,0.781186,0.820245,0.861257,0.904320,1.0)); //110-119
		public static ArrayList<Double> female_mortality_by_age = new ArrayList<Double>(
			Arrays.asList(0.005513,0.000382,0.000218,0.000166,0.000143,0.000127,0.000116,0.000106,0.000098,0.000091,   //0-9
                	0.000086,0.000089,0.000102,0.000128,0.000164,0.000205,0.000246,0.000285,0.000319,0.000350,   //10-19
	        	0.000383,0.000417,0.000446,0.000469,0.000487,0.000505,0.000525,0.000551,0.000585,0.000626,   //20-29
      	        	0.000672,0.000720,0.000766,0.000806,0.000846,0.000891,0.000946,0.001013,0.001094,0.001190,   //30-39
	        	0.001296,0.001413,0.001549,0.001706,0.001881,0.002069,0.002270,0.002486,0.002716,0.002960,   //40-49
	        	0.003226,0.003505,0.003779,0.004040,0.004301,0.004592,0.004920,0.005266,0.005630,0.006028,   //50-59
	        	0.006479,0.007001,0.007602,0.008294,0.009082,0.009990,0.011005,0.012097,0.013261,0.014529,   //60-69
	        	0.015991,0.017662,0.019486,0.021467,0.023658,0.026223,0.029159,0.032331,0.035725,0.039469,   //70-79
	        	0.043828,0.048896,0.054577,0.060909,0.068019,0.076054,0.085148,0.095395,0.106857,0.119557,   //80-89
	        	0.133502,0.148685,0.165088,0.182685,0.201442,0.220406,0.239273,0.257714,0.275376,0.291899,   //90-99
	        	0.309413,0.327978,0.347656,0.368516,0.390627,0.414064,0.438908,0.465243,0.493157,0.522747,   //100-109
	        	0.554111,0.587358,0.622599,0.659955,0.699553,0.741526,0.781186,0.820245,0.861257,0.904320,1.0)); //110-119

		//public static double []  fertility_by_age = new double[ ]{0.0076,0.0076,0.0076,0.0076,0.0076,0.421,0.421,0.421,0.421,0.421,0.9422,0.9422,0.9422,0.9422,0.9422,1,1,1,1,1,0.7606,0.7606,0.7606,0.7606,0.7606,0.3251,0.3251,0.3251,0.3251,0.3251,0.0628,0.0628,0.0628,0.0628,0.0628,0.0034,0.0034,0.0034,0.0034,0.0034,0.0034};
    //public static double [] male_marriage_by_age = new double[] {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.05,0.09,0.16,0.23,0.32,0.43,0.55,0.69,0.88,0.97,1.00,0.96,0.83,0.66,0.53,0.48,0.45,0.44,0.43,0.41,0.38,0.33,0.27,0.24,0.21,0.20,0.20,0.20,0.19,0.19,0.20,0.20,0.20,0.21,0.21,0.21};
    //public static double [] female_marriage_by_age = new double[] {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.04,0.08,0.20,0.48,0.86,1.00,0.97,0.82,0.67,0.55,0.49,0.46,0.45,0.42,0.38,0.35,0.30,0.25,0.22,0.19,0.18,0.17,0.17,0.16,0.15,0.14,0.13,0.13,0.12,0.11,0.10,0.10,0.09,0.09,0.08,0.07};
		public static ArrayList<Double> fertility_by_age = new ArrayList<Double>(
      Arrays.asList(0.0076,0.0076,0.0076,0.0076,0.0076,0.421,0.421,0.421,0.421,0.421,0.9422,0.9422,0.9422,0.9422,0.9422,1.0,1.0,1.0,1.0,1.0,0.7606,0.7606,0.7606,0.7606,0.7606,0.3251,0.3251,0.3251,0.3251,0.3251,0.0628,0.0628,0.0628,0.0628,0.0628,0.0034,0.0034,0.0034,0.0034,0.0034,0.0034));
    public static ArrayList<Double> male_marriage_by_age = new ArrayList<Double> (Arrays.asList(0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.05,0.09,0.16,0.23,0.32,0.43,0.55,0.69,0.88,0.97,1.00,0.96,0.83,0.66,0.53,0.48,0.45,0.44,0.43,0.41,0.38,0.33,0.27,0.24,0.21,0.20,0.20,0.20,0.19,0.19,0.20,0.20,0.20,0.21,0.21,0.21));
    public static ArrayList<Double> female_marriage_by_age = new ArrayList<Double> (Arrays.asList(0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.04,0.08,0.20,0.48,0.86,1.00,0.97,0.82,0.67,0.55,0.49,0.46,0.45,0.42,0.38,0.35,0.30,0.25,0.22,0.19,0.18,0.17,0.17,0.16,0.15,0.14,0.13,0.13,0.12,0.11,0.10,0.10,0.09,0.09,0.08,0.07));
  }

	/*
	* The population object represents a simulated breeding poplation of humans.
	* All individuals within the same population have equal chances to come in contact and mate  
	*/
	public static class Population
	{
		private Random rand  = new Random();
		private ArrayList<Double> male_mortality_by_age;
		private ArrayList<Double> female_mortality_by_age;
		private HashMap<Integer, Double> male_mortality_by_age_map = new HashMap<Integer, Double>();
		private HashMap<Integer, Double> female_mortality_by_age_map = new HashMap<Integer, Double>();

		private ArrayList<Double> fertility_by_age;
		private HashMap<Integer, Double> ageFertility = new HashMap<Integer,Double>();
		private HashMap<Integer, Double> ageMarriageF = new HashMap<Integer,Double>();
		private HashMap<Integer, Double> ageMarriageM = new HashMap<Integer,Double>();
		private ArrayList<Individual>  populace;
		private ArrayList<Individual> males;
		private ArrayList<Individual> married_females;
		private ArrayList<Individual> married_males;
		private ArrayList<Individual> females;
		private ArrayList<Individual> juveniles;
		private ArrayList<String> female_names;
		private ArrayList<String> male_names;
		private ArrayList<String> last_names;
		private int size;
		private int fertilityStart = 15;
		private int fertilityEnd  = 49;
		double failratio=0;
		private int startsize;
    //private int year;
		//private int startyear;
		private double birthrate;
		private double marriagerate;
		private double divorcerate = 0.0028;
		private double deathrate = 0.01;
		private double internal_migration_rate = 0.001;
		private double immigrationrate = 0.01;
		private double emigrationrate= 0.01;
		private int totMigrants = 0;
		private double marriage_hitrate = 0;
		private double full_sibling_rate;
		private String name;
		private int latitude;
		private int longitude;
		private double partsingles;
		private double partmarried;
		private double avgChildren;
		private double avgPatSib;
		private double avgMatSib;
		private double avgFullSib;
		private double avgDeg1;
		private double avgDeg2;
		private double avgDeg3;
		private StringBuilder annual;
		private StringBuilder popsize;
		private Region region;
    private int ascertainment_order;
	/*
	* Constructor for populations 
	*/
		public Population(
				Region region,
				String name,
				int size,
        int ascertainment_order,
				//int startyear, 
				double birthrate,
				double deathrate, 
				double marriagerate,
				double immigrationrate,
				double emigrationrate,
				double full_sibling_rate,
        double internal_migration_rate,
        double divorcerate,
        int fertilityStart,
        int fertilityEnd,
        ArrayList<Double> male_mortality_by_age,
        ArrayList<Double> female_mortality_by_age,
        ArrayList<Double> male_marriage,
        ArrayList<Double> female_marriage,
        ArrayList<Double> fertility,
				ArrayList<String> female_names, 
				ArrayList<String> male_names,
				ArrayList<String> last_names)
        {
			this.region = region;
			this.name = name;
			this.startsize = size;
			this.size = size;
			this.ascertainment_order = ascertainment_order;
			//this.startyear = startyear;
			//this.year = startyear;
			this.birthrate = birthrate;
			this.deathrate = deathrate; 	
			this.marriagerate = marriagerate;// normal rate 0.0068
			this.immigrationrate = immigrationrate; 	
			this.emigrationrate = emigrationrate; 	
			this.full_sibling_rate = full_sibling_rate;
      this.internal_migration_rate = internal_migration_rate;
      this.divorcerate = divorcerate;
      this.fertilityStart = fertilityStart;
      this.fertilityEnd = fertilityEnd;
      this.male_mortality_by_age = male_mortality_by_age;
      this.female_mortality_by_age = female_mortality_by_age;

			this.male_names = male_names;
			this.female_names = female_names;
			this.last_names = last_names;
			
      this.populace = new ArrayList<Individual>();
			this.juveniles = new ArrayList<Individual>();
			this.males = new ArrayList<Individual>();
			this.females = new ArrayList<Individual>();
			this.married_males = new ArrayList<Individual>();
			this.married_females = new ArrayList<Individual>();
			this.annual = new StringBuilder();
			this.popsize = new StringBuilder();
			this.popsize.append(name+"\t"+size);
			int seedSize = (int)Math.round((double)size/4);

      for (int p = 0; p < male_mortality_by_age.size();p++) 
			{
				male_mortality_by_age_map.put(p, male_mortality_by_age.get(p));
				female_mortality_by_age_map.put(p, female_mortality_by_age.get(p));
			}

      // Load fertility rate lookup tables
			for (int i = 0; i < fertility.size(); i++)
			{
				ageFertility.put(i, fertility.get(i)); 
			}
      for(int i = fertility.size(); i <= 120; i++)
      {
				ageFertility.put(i, 0.0); 
      }

			// Load marriage rate lookup tables
		  for (int i = 0; i < female_marriage.size(); i++)
			{
        ageMarriageF.put(i, female_marriage.get(i));
			}
      for(int i = female_marriage.size(); i <= 120; i++)
      {
				ageMarriageF.put(i, 0.0); 
      }

      for (int i = 0; i < male_marriage.size(); i++)
			{
        ageMarriageM.put(i, male_marriage.get(i));
			}	
      for(int i = male_marriage.size(); i <= 120; i++)
      {
				ageMarriageM.put(i, 0.0); 
      }

      System.out.println("year: " + this.region.getYear());
			for ( int i=0 ; i < size; i++)
			{
				int age  = Math.abs(  rand.nextInt()%this.getFertilityEnd());

        if( (this.region.getYear() - (this.region.getYear()-age)) > config.fertilityEnd)
        {
          System.out.println("ERROR: Initialized age > fertilityEnd: " + age);
        }
				addMember(new Individual(
				i%2, 
				this,
				this.region.getYear()-age));
			}  
			
		}
	/*
	* Custom Print function for Population objects.
	*/
 		public String toString()
		{
			StringBuilder popString = new StringBuilder("Population info:\n");
			popString.append("          "+this.name+"\n");
			popString.append(" Start Size           : "+this.startsize+"\n");
			popString.append(" Current Size         : "+this.size+"\n");
			popString.append(" Single males         : "+this.males.size()+"\n");
			popString.append(" Single females       : "+this.females.size()+"\n");
			popString.append(" Married males        : "+this.married_males.size()+"\n");
			popString.append(" Married females      : "+this.married_females.size()+"\n");
      popString.append(" % matingPool married : "+(int)((double)(this.married_males.size()+this.married_females.size())/(double)this.getPopulationFertilePoolSize()*100) + "\n");
			popString.append(" Juveniles            : "+this.juveniles.size()+"\n");
			popString.append(" MatingPool           : "+this.region.matingPool.size()+"\n");
			popString.append(" AgedPool             : "+this.region.agedPool.size()+"\n");
			popString.append(" Current Year         : "+this.region.getYear()+"\n");
			popString.append(" Birth Rate           : "+this.birthrate+"\n");
			popString.append(" Death Rate           : "+this.deathrate+"\n");
			popString.append(" Marriage Rate        : "+this.marriagerate+"\n");
			popString.append(" Immigration Rate     : "+this.immigrationrate+"\n");
			popString.append(" Emigration Rate      : "+this.emigrationrate+"\n");
			popString.append(" Full sibling rate    : "+this.full_sibling_rate+"\n");
			popString.append(" Avg # children       : "+this.avgChildren+"\n");
			popString.append(" Avg deg 1 relatives  : "+this.avgDeg1+"\n");
			popString.append(" Avg deg 2 relatives  : "+this.avgDeg2+"\n");
			popString.append(" Avg deg 3 relatives  : "+this.avgDeg3+"\n");
			return popString.toString();
		}

    public int getPopulationFertilePoolSize()
    {
      return this.married_males.size() + this.married_females.size() +  this.females.size() +  this.males.size();
    }

    /*
    * Returns the population name
    */
		public String getName()
		{	
			return this.name;
		}

    /*
    * Sets the population name
    */
		public void setName(String name)
		{	
			 this.name = name;
		}

    /*
    * Returns the ascertaiment order
    */
		public int get_ascertainment_order()
		{	
			return this.ascertainment_order;
		}

    /*
    * Simulates the passage of one year in a population.  
    * Ages each individual in the population by one year and then simulates marriages, conceptions, and deaths in that order. 
    */
		public void elapseYear(boolean keepStatic, int survey)
		{
			int beginSize = this.getSize();
			long start = System.nanoTime();
			int totChildren = 0;
			int totMatSib = 0;
			int totPatSib = 0;
			int totFullSib = 0;
			int totDeg1 = 0;
 			int totDeg2 = 0;
			int totDeg3 = 0;

      System.out.println("pop year: " + this.region.getYear());

			// call population scale methods and record runtimes for each
      System.out.println("Before: " +  this.getSize());
      if(this.married_males.size() != this.married_females.size())
      {
        System.out.println("ERROR: # married males/females: " + this.married_males.size() + "/" + this.married_females.size());

        System.exit(1);
      }
      //System.out.println("BEFORE COURT:\n" + this.toString());

      //System.out.println("juv size before: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());
			long preCourt = System.nanoTime();
			int court = court(keepStatic);
      //System.out.println("After court: " +  this.getSize());
		  //System.out.println("# married males/females: " + this.married_males.size() + "/" + this.married_females.size());
      //System.out.println("juv size: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());
      //System.out.println("AFTER COURT:\n" + this.toString());
  
      long preSplit = System.nanoTime();
			int split = split(keepStatic);
      //System.out.println("After split: " +  this.getSize());
		  //System.out.println("# married males/females: " + this.married_males.size() + "/" + this.married_females.size());
      //System.out.println("juv size: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());
      //System.out.println("AFTER SPLIT:\n" + this.toString());
      
      long preMingle = System.nanoTime();
			int mingle = mingle();
      //System.out.println("After mingle: " +  this.getSize());
		  //System.out.println("# married males/females: " + this.married_males.size() + "/" + this.married_females.size());
      //System.out.println("juv size: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());

			long preCull = System.nanoTime();
			int cull = cull(mingle,keepStatic);
      //System.out.println("After cull: " +  this.getSize());
		  //System.out.println("# married males/females: " + this.married_males.size() + "/" + this.married_females.size());
      //System.out.println("juv size: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());
			
			long preMigrate = System.nanoTime();
			int migrate = migrate(keepStatic);
      //System.out.println("After migrate: " +  this.getSize());
		  //System.out.println("# married males/females: " + this.married_males.size() + "/" + this.married_females.size());
      //System.out.println("juv size: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());
			
      long preTransplant = System.nanoTime();
      int transplant = transplant();
      //System.out.println("After transplant: " +  this.getSize());
		  //System.out.println("# married males/females: " + this.married_males.size() + "/" + this.married_females.size());
      //System.out.println("juv size: " + this.juveniles.size() + " " + this.region.juvenilesPool.size());
      //System.out.println("AFTER ALL:\n" + this.toString());
			
      long finish = System.nanoTime();
      
      System.out.println("court: " + court);
      System.out.println("split: " + split);
      System.out.println("cull: " + cull);
      System.out.println("mingle: " + mingle);
      System.out.println("migrate: " + migrate);
      System.out.println("transplant: " + transplant);
      System.out.println("\n");

			// calculate runtimes for each subroutine
			long totalTime = finish - start;
			long loopTime = preCourt - start;  
			long courtTime = preSplit - preCourt; 
			long splitTime = preMingle - preSplit; 
			long mingleTime = preCull - preMingle; 
			long cullTime = preMigrate - preCull; 
			long migrateTime = preTransplant - preMigrate; 
			long transplantTime = finish - preTransplant;
			
			// update data and output 

			this.partsingles = ((double)this.males.size()+(double)this.females.size())/(double)this.size;
			this.partmarried = ((double)this.married_males.size()+(double)this.married_females.size())/(double)this.size;
			this.size = this.populace.size();
			this.totMigrants += Math.abs(migrate);
			/*
			if (this.region.getPops().size() > 1) System.out.println(this.name);
			System.out.println(court+ " Marriages");
			System.out.println(split+ " Divorces");
			System.out.println(mingle+ " Births");
			System.out.println(transplant + " Relocations");
			System.out.println(cull+" Deaths");
			System.out.println("Population: "+this.size);
			System.out.println(migrate + " Net immigrants");
			System.out.println(totMigrants + " Total migrants");
			System.out.println("Population change: " +(this.size - beginSize));
			*/
			System.out.println("\tLoop time: " +loopTime+" ("+Math.round(((double)loopTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tCourt time: " +courtTime+" ("+Math.round(((double)courtTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tSplit time: " +splitTime+" ("+Math.round(((double)splitTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tMingle time: " +mingleTime+" ("+Math.round(((double)mingleTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tCull time: " +cullTime+" ("+Math.round(((double)cullTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tMigrate time: " +migrateTime+" ("+Math.round(((double)migrateTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tTransplant time: " +transplantTime+" ("+Math.round(((double)transplantTime / (double)totalTime)*100)+"%)" ); 
			System.out.println("\tTotal time: " + (double)totalTime/1000000000 + " s");
			//System.out.println("Mating age population\n" + this.getMatingAge());
			this.popsize.append("\t"+this.size);
			
      
			if (this.region.getYear()%survey == 0 )
			{
				for (Individual i : this.populace)
				{
					totChildren += i.getChildren().size();
					totPatSib += i.getPaternalSiblings().size();
					totMatSib += i.getMaternalSiblings().size();
					totFullSib += i.getFullSiblings().size();
					totDeg1 += i.getRelatives(1).size();
					totDeg2 += i.getRelatives(2).size();
					totDeg3 += i.getRelatives(3).size();
				}	
			}

			// calculate averages
			this.avgChildren = totChildren / (double) this.size;
			this.avgPatSib = totPatSib / (double) this.size;
			this.avgMatSib = totMatSib / (double) this.size;
			this.avgFullSib = totFullSib / (double) this.size;
			this.avgDeg1 = totDeg1 / (double) this.size;
			this.avgDeg2 = totDeg2 / (double) this.size;
			this.avgDeg3 = totDeg3 / (double) this.size;
			
      
      if (this.region.getYear()%survey == 0)
			{	
				this.annual.append(this.region.getYear()+"\t"+this.size+"\t"+court+"\t"+mingle+"\t"+cull+"\t");
				this.annual.append(totPatSib+"\t"+totMatSib+"\t"+totFullSib+"\t"+this.avgChildren+"\t");
				this.annual.append(this.avgDeg1+"\t"+this.avgDeg2+"\t"+this.avgDeg3+"\t"+this.partsingles+"\t"+this.partmarried+"\t"+this.marriage_hitrate+"\n");
			}
      System.out.println(this.toString());
		}

    /*
    * closes the printwriter for year by year stats
    */
		public String getDataString()
		{
			return this.annual.toString();
		}
		public String getPopString()
		{
			return this.popsize.toString();
		}

    /*
    * Returns the region that the population is located in 
    */
		public Region getRegion()
		{
			return this.region;
		}

    /*
    * Returns the age at which an individual may join the population's mating pool 
    */
		public int getFertilityStart()
		{
			return this.fertilityStart;
		}

    /*
    * Returns the age at which an individual leaves the population's mating pool 
    */
		public int getFertilityEnd()
		{
			return this.fertilityEnd;
		}
/*
    public String getNewName( Individual i )
    {
      String name;
			int found_name = 0;
      while(found_name == 0)
      {
        if (i.getGender() == 0)
        {
          String name = female_names.get(nameIndex%female_names.size());

          if (i.getName() == null)i.setName();
        }
        else 
        {
          if (i.getName() == null)i.setName(male_names.get(nameIndex%male_names.size()));
        }
        
        String id = i.toString();
        if(id.equals()
      }

    }
*/
    /*
    * Adds a new member to a population. Called by both the Population Constructor
    * and the conceive() method.  
    */
		public void addMember( Individual i )
		{
			this.juveniles.add(i);

			if (i.isFertile()) 
      {
        this.joinPopulationLevelPools(i); 
      }

			i.setBirthYear(this.region.getYear() - i.getAge());
			int nameIndex = Math.abs(rand.nextInt());
			populace.add(i);

      // Set last name if not already set
			int surnameIndex = Math.abs(rand.nextInt());
			if (i.getSurname() == null)i.setSurname(last_names.get(surnameIndex%last_names.size()));

      // Set first name if not alreadu set
			if (i.getGender() == 0)
			{
				if (i.getName() == null)i.setName(female_names.get(nameIndex%female_names.size()));
			}
			else 
			{
				if (i.getName() == null)i.setName(male_names.get(nameIndex%male_names.size()));
			}
      //System.out.println("addMember " + i + " ");
		}

    /*
    * Simulates an individual reaching the age of reproductive capacity or immigrating into the population
    */
		public void joinPopulationLevelPools(Individual i)
		{
      if (i.isDead()) return;

      if (!i.isFertile() && !this.juveniles.contains(i)) 
      {
        this.juveniles.add(i);
        return;
      }

      // In case the individual is in the population juveniles pool
      this.juveniles.remove(i);

      if (i.isMarried() == true)
      {
        if (i.getGender() == 0 && !this.married_females.contains(i))
        {
          this.married_females.add(i);
        }
        if(i.getGender() == 1 && !this.married_males.contains(i))
        {
          this.married_males.add(i);
        }
      }
      else
      {
        if (i.getGender() == 0 && !this.females.contains(i) && !i.isMarried())
        {
          this.females.add(i);
        }
        if(i.getGender() ==1 && !this.males.contains(i) && !i.isMarried()) 
        {
          this.males.add(i);
        }
      }
		}

    /*
    * Simulates an individual aging out of reproductive capacity. 
    */
		public void leavePopulationLevelPools(Individual i)
		{
      if(i.getSpouse() != null)
      {
        this.split_from_spouse(i);
      }
			this.juveniles.remove(i);
			this.females.remove(i);
			this.males.remove(i);
			this.married_females.remove(i);
			this.married_males.remove(i);
			//this.married_females.remove(i.getSpouse());
			//this.married_males.remove(i.getSpouse());
      //i.getSpouse().setSpouse(null);
		}
	/*
	* Returns the relative probability of conception by age of the mother
	* O(1)
	*/
		public double getFertility(int age)
		{
			return ageFertility.get(age);
		}
	/*
	* Returns the probability of marriage for a given age and gender
	* O(1)
	*/
		public double getMarriage(int age, int gender)
		{
			if (gender == 0)return ageMarriageF.get(age);
			else return ageMarriageM.get(age);
		}
	/*
	* Returns the current year 
	*/
		public int getYear()
		{
			return this.region.getYear();
		}
	/*
	* Returns the list of available names for a given gender
	* O(1)
	*/
		public ArrayList<String> getNames(int gender)
		{
			if (gender == 0) return this.female_names;
			else return this.male_names;
		}
	/*
	* Returns the current population size
	*/
		public int getSize()
		{
			return populace.size();
		}
	/*
	* returns the arraylist of individuals that form the living population
	*/
		public ArrayList<Individual>  getPopulace()
		{
			return this.populace;	
		}
	/*
	* returns the arraylist of individuals that form the living population
	*/
		public ArrayList<Individual>  getJuveniles()
		{
			return this.juveniles;	
		}
	/*
	* sets the marrige rate for the population
	*/
		public void setmarriagerate(double rate)
		{
			this.marriagerate = rate; 
		}
	/*
	* sets the birth rate for the population
	*/
		public void setbirthrate(double rate)
		{
			this.birthrate = rate; 
		}
	/*
	* sets the birth rate for the population
	*/
		public void setdeathrate(double rate)
		{
			this.deathrate = rate; 
		}
	/*
	* sets the immigration rate for the population
	*/
		public void setimmigrationrate(double rate)
		{
			this.immigrationrate = rate; 
		}
	/*
	* sets the emigration rate for the population
	*/
		public void setemigrationrate(double rate)
		{
			this.emigrationrate = rate; 
		}
	/*
	* returns the arraylist of reproductive age individuals 
	*/
		public ArrayList<Individual>  getMatingAge()
		{
			ArrayList<Individual> matingAge = new ArrayList<Individual>();
			matingAge.addAll(this.males);
			matingAge.addAll(this.females);
			matingAge.addAll(this.married_males);
			matingAge.addAll(this.married_females);
      //Collections.sort(matingAge, new Comparethem());
			return matingAge;	
		}
	/*
	* returns the arraylist of beyond reproductive age individuals 
	*/
		public ArrayList<Individual>  getAged()
		{
			ArrayList<Individual> aged = new ArrayList<Individual>();
			aged.addAll(this.getPopulace());
      System.out.println("# of individuals: " + aged.size()); 

			aged.removeAll(this.juveniles);
      System.out.println("# of individuals (non-juveniles): " + aged.size()); 
			aged.removeAll(this.getMatingAge());
      System.out.println("# of post-mating age individuals: " + aged.size()); 
			Collections.sort(aged, new Comparethem());
      //System.out.println("aged Size4: " + aged.size()); 
			return aged;	
		}
	/*
	* Simulates all of the deaths that take place in a population in one year. 
	* Each age is mapped to a probability of death. When this method is called, a random number 
	* between zero and one is generated for each individual. If this random number does not exceed
 	* the probability of death for that person's age, they are removed from the population 
	* as well as any other pools that may belong to based on their gender and marital status. 
	* this processed is repeated until the set deathrate has been reached. 
	* Returns the number of individuals that died.
	* O(N) 
	*/
		public int cull(int mingle, boolean keepStatic)
		{ 
			int beginSize = this.size; 
			int number_dead =(int)Math.round((double)this.size*this.deathrate);
			if(keepStatic) number_dead = mingle; // Set number to die to match the number that were born
			int numDead =0;
			double required = 1;  
			ArrayList<Individual> deceased = new ArrayList<Individual>();

      // Kill off anyone over 120 first, but make sure the person is in this population
			for (int i = this.region.agedPool.size()-1 ;i >= 0; i--)
			{
				Individual person = this.region.agedPool.get(i);
				if (person.getAge() < 120)
        {
          break;
        }

        if (person.getResidence() == this)
				{
          numDead++;
          person.passOn();
          //System.out.println(person + " died of old age");
          this.leavePopulationLevelPools(person);
          deceased.add(person);
          this.populace.remove(person);
				}
			}

			while (numDead < number_dead)
			{
				Individual i = this.populace.get(Math.abs(rand.nextInt()%this.populace.size()));
				Double token = rand.nextDouble();
				if (i.getGender() == 0)
				{
					if (female_mortality_by_age_map.keySet().contains(i.getAge()))
						required =  female_mortality_by_age_map.get(i.getAge());
					if (token <= required)
					{
						numDead++;
            //System.out.println(i + " was culled");
						i.passOn();
            //System.out.println("Is in female? " + this.females.contains(i));
            //System.out.println("Is in married_females? " + this.married_females.contains(i));
						this.leavePopulationLevelPools(i);
            //System.out.println("Is in female? " + this.females.contains(i));
            //System.out.println("Is in married_females? " + this.married_females.contains(i)+"\n");
						deceased.add(i);
						this.populace.remove(i);
					}
				} 
				if (i.getGender() == 1)
				{
					if (male_mortality_by_age_map.keySet().contains(i.getAge()))
						required =  male_mortality_by_age_map.get(i.getAge());
					if (token <= required)
					{
						numDead++;
            //System.out.println(i + " was culled");
						i.passOn();
						this.leavePopulationLevelPools(i);
						deceased.add(i);
						this.populace.remove(i);
					}
				}
			}
			this.juveniles.removeAll(deceased);
			this.region.juvenilesPool.removeAll(deceased);
			this.region.matingPool.removeAll(deceased);
			this.region.agedPool.removeAll(deceased);
			return numDead;
		}

    /*
    * Simulates all of the marriages that take place in a population in one year. 
    * Husband/Wife pairs are chosen at random from the pool of single reproductive age males and females.
    * Pairs are drawn until either the number of succesful marriges is reached as defined by marriage rate 
    * or until the time limit is reached. 
    * Returns the number of Successful Marriages.  
    */
		public int court(boolean keepStatic)
		{
			if (this.males.size() < 3 || this.females.size() < 3) {return 0;}
			int attempts = 0; 
			int failures = 0;  
			int related_failures = 0;  
			int marriages = 0;
			int number_married = (int)Math.round(this.marriagerate*(double)this.size);

      // Double the marriage rate during burn-in until average_population_married of popualtion is married
      if(keepStatic) 
      {
        int num_married = this.married_males.size() + this.married_females.size();
        if((double)num_married/(double)this.getPopulationFertilePoolSize() < config.proportion_initially_married) 
        {
          System.out.println("Doubling marriages");
          number_married = number_married * 2;
        }
      }
      System.out.println("# expected marriages: " + number_married);
			Double sum_time = 0.0;
      long Start = System.nanoTime();
			while(marriages < number_married  && !this.males.isEmpty()&& !this.females.isEmpty())
			{
				Individual husband = this.males.get(Math.abs(rand.nextInt()%this.males.size()));	
        double token = husband.rn.nextDouble();
        if (token > husband.residence.getMarriage(husband.getAge(), husband.gender))
        {
          continue;
        }
				for (int w= 0;  w < this.females.size(); w++)
				{	
					attempts++;
					Individual wife = this.females.get(w);
          token = wife.rn.nextDouble();
          if (token > wife.residence.getMarriage(wife.getAge(), wife.gender))
          {
            continue;
          }

					if (husband.marry(wife))
					{
						marriages++;
						break;
					}
					else
					{
						failures++;
						if(wife.isRelated(husband,3))related_failures++;
					}
				}
			}
      long Stop = System.nanoTime();
			Collections.shuffle(this.females);
			if (failures > 0) failratio = (double)related_failures / (double) failures;
			double hitrate = (double)marriages/(double)attempts;
      System.out.println("Marriage time: " + ((double)Stop - Start)/1000000000 + " s");
      System.out.println("Marriage hit rate: " + hitrate);
			System.out.println("% failures due to relatedness " + failratio);
			this.marriage_hitrate = hitrate; 
			return marriages;
		}

    /*
    * Simulates all of the reproduction that may take place within a population for one year. 
    * Mother/Father pairs are chosen at random from either the single reproductive age pool
    * or the married pool in a ratio defined by full_sibling_rate. 
    * Pairs are drawn and reproduction attempts are made until either the number of succesful conceptions 
    * defined by the population birthrate is reached or the time limit is reached. 
    * Returns the number of successful conceptions. 
    */
		public int mingle()
		{
			ArrayList<Individual> available_females = new ArrayList<Individual>();
			available_females.addAll(this.females);
			ArrayList<Individual> available_males = new ArrayList<Individual>();
			available_males.addAll(this.males);
			ArrayList<Individual> available_married_females = new ArrayList<Individual>();
			available_married_females.addAll(this.married_females);
			ArrayList<Individual> available_married_males = new ArrayList<Individual>();
			available_married_males.addAll(this.married_males);
			
      if (available_males.size() < 1 || available_females.size() < 1) {return 0;}
			
      int successes = 0;
      int number_born = (int)Math.round(this.birthrate*((double)this.size));
      //System.out.println("Number to be born: " + number_born);


			int full_sib_target = (int)Math.round(this.full_sibling_rate*(double)number_born); 
			int half_sib_target = number_born - full_sib_target;
			int num_full_sib = 0;
			int num_half_sib = 0;
			int singInd = 0;
			int numConceived = 0;
			int singleAttempts = 0;
			int marriedAttempts = 0; 
			Individual single_father;
			Individual single_mother;
			Individual married_father;
			Individual married_mother;
			long Start = System.currentTimeMillis();
			while(num_full_sib < full_sib_target && !available_married_males.isEmpty() && !available_married_females.isEmpty())
			{
        //System.out.println("MARRIED");
				if ( num_full_sib >= 0.5*(double) available_married_females.size()) break;	
				marriedAttempts++;
				married_father = available_married_males.get(Math.abs(rand.nextInt()%available_married_males.size()));
				married_mother = married_father.getSpouse();
        

        if (!married_mother.isFertile())
        {
          continue;
          //System.out.println("Is in fertile? " + this.region.matingPool.contains(married_mother));
          //System.out.println("Is in aged? " + this.region.agedPool.contains(married_mother));
          //System.out.println("Is in married_female? " + this.married_females.contains(married_mother));
          //System.out.println("Mating Failure (" + married_mother.immigrant +" " + married_mother.isDead() +") : infertile");
          //System.exit(1);
        }

				if (married_father.mate(married_mother) && married_mother.mate(married_father))
				{
					numConceived = married_mother.conceive(married_father, true);
					successes += numConceived;
					available_married_males.remove(married_father);
					num_full_sib += numConceived;
				}
			}
			while(num_half_sib < half_sib_target && !available_males.isEmpty() && !available_females.isEmpty())
			{
        //System.out.println("UNMARRIED");
				if (num_half_sib >= 0.5*(double) available_females.size()) break;
				single_father = available_males.get(Math.abs(rand.nextInt()%available_males.size()));	
				if (singInd >= available_females.size()) singInd =0;  
				for (int m = singInd; m < available_females.size(); m++)
				{
					single_mother = available_females.get(m);
          if (!single_mother.isFertile())
          {
            //continue;
            System.out.println("Is in fertile? " + this.region.matingPool.contains(single_mother));
            System.out.println("Is in aged? " + this.region.agedPool.contains(single_mother));
            System.out.println("Is in female? " + this.females.contains(single_mother));
            System.out.println("Is in married_female? " + this.married_females.contains(single_mother));
            System.out.println("Mating Failure ("+ single_mother + " " + single_mother.immigrant +" " + single_mother.isDead() +") : infertile");
            System.exit(1);
          }
					singleAttempts++;
					if (single_mother.mate(single_father) && single_father.mate(single_mother))
					{
						singInd = m+1; 
						numConceived = single_mother.conceive(single_father, false);
						num_half_sib+= numConceived;
						successes+= numConceived;
						available_females.remove(single_mother);
						available_males.remove(single_father);
						break;
					}
				}
			}
			//System.out.println("Single success rate: "+ (double)num_half_sib/(double)singleAttempts); 
			//System.out.println("Married success rate: "+ (double)num_full_sib/(double)marriedAttempts); 
			//System.out.println( "out of full_sib target: " + (double)half_sib_target);
			//System.out.println( "Births out of half_sib_target: " + (double)num_half_sib);
			//System.out.println( "number successes: " + successes);
			return successes;
		}

		public void split_from_spouse(Individual ind)
		{	
      Individual spouse = ind.getSpouse();
      
      ind.setSpouse(null);
      spouse.setSpouse(null);
      this.married_females.remove(ind);
      this.married_males.remove(ind); 
      this.married_females.remove(spouse);
      this.married_males.remove(spouse); 
      
      if (ind.isFertile())
      { 
        //System.out.println("join pools " + ind);
        this.joinPopulationLevelPools(ind);
      }
      if (spouse.isFertile())
      {
        //System.out.println("join pools " + spouse);
        this.joinPopulationLevelPools(spouse);
      }
		}

	/*
	* Simulates all of the divorces that take place in a population within a given year
	*/
		public int split(boolean keepStatic)
		{	
      //int num_married = this.married_males.size() + this.married_females.size();
      //double temp_proportion_initially_married = (double)num_married/(double)this.getPopulationFertilePoolSize();
      
      //if(temp_proportion_initially_married < config.proportion_initially_married)
      //{
        //return 0;
      //}

      //int toSplit = (int)Math.round((temp_proportion_initially_married - config.proportion_initially_married) * (double)this.region.matingPool.size());
      int toSplit = (int)Math.round(config.divorcerate * (double)this.getPopulationFertilePoolSize());
      
      //System.out.println("Divorse: " + toSplit + " = " + temp_proportion_initially_married + " - " + config.proportion_initially_married + " " + (temp_proportion_initially_married - config.proportion_initially_married) + " x " + this.region.getPopulationFertilePoolSize());

			int split = 0;
			Individual husband; 
			Individual wife; 
			while (split < toSplit && !this.married_females.isEmpty())
			{
				split++;
				husband = this.married_males.get(Math.abs(rand.nextInt()%this.married_males.size())); 
        this.split_from_spouse(husband);
			}	
			return split;
		}
	/*
	* Simulates immigration into and out of the population in one year
	*/
		public int migrate(boolean keepStatic)
		{
			int incoming = (int)Math.round((double) (this.size)*this.immigrationrate);
			int outgoing = (int)Math.round((double) (this.size)*this.emigrationrate);
      //System.out.println("migrate net before: " + net);
      //System.out.println("incoming before: " + incoming);
      //System.out.println("outgoing before: " + outgoing);
			int in = 0;
			int out = 0;
      int married_couples_out = 0;

      while (out < outgoing && !this.populace.isEmpty() )
      {
        Individual emigrant = this.populace.get(Math.abs(rand.nextInt()%this.populace.size()));
				Individual spouse = emigrant.getSpouse();
        if(out + 1 == outgoing && spouse != null && !spouse.isDead())
        {
          continue;
        }

        if(spouse != null && !spouse.isDead()) 
        {
          //System.out.println("Removing spouse: " + spouse);
          this.populace.remove(spouse);
          this.leavePopulationLevelPools(spouse);
          this.region.juvenilesPool.remove(spouse);
          this.region.matingPool.remove(spouse);
          this.region.agedPool.remove(spouse);
          //System.out.println(spouse + " was spouse moved");
          spouse.passOn();
          out++;
          married_couples_out++;
        }
        this.populace.remove(emigrant);
        this.leavePopulationLevelPools(emigrant);
        this.region.juvenilesPool.remove(emigrant);
        this.region.matingPool.remove(emigrant);
        this.region.agedPool.remove(emigrant);
        //System.out.println(emigrant + " was moved");
        emigrant.passOn();
        out++;
      }
      //System.out.println("After out: " +  this.getSize());
        
      if(keepStatic)
      {
        incoming = out;
      }

      int total = this.getSize();
      double proportion_juveniles = (double)this.juveniles.size() / (double)total;
      double proportion_fertile = (double)this.getPopulationFertilePoolSize() / (double)total;
      double proportion_aged = (double)(total - this.juveniles.size() - this.getPopulationFertilePoolSize())/(double)total;
      System.out.println("Proprotion immigrants (juvenile:fertile:aged) = " + proportion_juveniles + ":" + proportion_fertile + ":" + proportion_aged);



      // Immigrate juveniles
      for(int num = 0; num < incoming * proportion_juveniles; num++)
      {
        int index  = Math.abs( rand.nextInt()%this.region.juvenilesPool.size());
        Individual temp = this.region.getJuvenilesPool().get(index);
        Individual immigrant =  new Individual(
            Math.abs(rand.nextInt()%2),
            this, 
            temp.birthYear );
        
        this.region.addJuvenileToRegionLevelSortedJuvenilePool(immigrant, index);
        //System.out.print(this.region.getMatingPool());
        this.addMember(immigrant);
        //this.joinPopulationLevelPools(immigrant);
        immigrant.setImmigrant(true);
        in++;
      }
      System.out.println("post juvenile immigrants: " + in);
      
      // Add aged
      for(int num = 0; num < incoming * proportion_aged; num++)
      {
        int index  = Math.abs( rand.nextInt()%this.region.agedPool.size());
        Individual temp = this.region.getAgedPool().get(index);
        Individual immigrant =  new Individual(
            Math.abs(rand.nextInt()%2),
            this, 
            temp.birthYear );
        
        this.region.addAdultToRegionLevelSortedAgedPool(immigrant, index);
        //System.out.print(this.region.getMatingPool());
        this.addMember(immigrant);
        //this.populace.add(immigrant);
        immigrant.setImmigrant(true);
        in++;
      }
      System.out.println("post aged immigrants: " + in);
      
      // Add couples
      for(int num = 0; num < married_couples_out; num++)
      {
        int index1 = Math.abs( rand.nextInt()%this.region.getMatingPool().size());
        Individual temp1 = this.region.getMatingPool().get(index1);
        Individual wife =  new Individual(
            0,
            this, 
            temp1.birthYear );
        this.region.addAdultToRegionLevelSortedMatingPool(wife, index1);


        int index2 = Math.abs( rand.nextInt()%this.region.getMatingPool().size());
        Individual temp2 = this.region.getMatingPool().get(index2);
        Individual husband =  new Individual(
            1,
            this, 
            temp2.birthYear );
        this.region.addAdultToRegionLevelSortedMatingPool(husband, index2);

        this.addMember(wife);
        this.addMember(husband);
        wife.setImmigrant(true);
        husband.setImmigrant(true);
        husband.marry(wife);
        in++;
        in++;
        if(in + 1 >= incoming)
        {
          break;
        } 
      }
      System.out.println("post married immigrants: " + in);
      

      // Add single fertile aged to fill the gap
      while (in < incoming)
      {
        int index  = Math.abs( rand.nextInt()%this.region.getMatingPool().size());
        Individual temp = this.region.getMatingPool().get(index);
        
        Individual immigrant =  new Individual(
            Math.abs(rand.nextInt()%2),
            this, 
            temp.birthYear );
        if (immigrant.isFertile())
        {
          this.region.addAdultToRegionLevelSortedMatingPool(immigrant, index);
          //System.out.print(this.region.getMatingPool());
          this.addMember(immigrant);
          //this.populace.add(immigrant);
          //this.joinPopulationLevelPools(immigrant);
          immigrant.setImmigrant(true);
          in++;
        }
      }
      System.out.println("post single immigrants: " + in);
      //System.out.println("After in: " +  this.getSize());
			int net = in - out;
      System.out.println("Outgoing:incoming:net"+ out + ":" + in + ":" + net);
      return net;
		}

    /*
    * Simulates all of the between population movement that may take place within one year
    */
		public int transplant()
		{
			Individual traveler; 
			int moved = 0; 
			int toMove = (int)Math.round((this.internal_migration_rate*(double)this.region.getSize())/this.region.getPops().size());
      //System.out.println("num to move: " + toMove);
			ArrayList<Population> elsewhere = new ArrayList<Population>();
			ArrayList<Individual> wayfarers = new ArrayList<Individual>();
			wayfarers.addAll(this.males);
			wayfarers.addAll(this.females);
			elsewhere.addAll( this.region.getPops());
			elsewhere.remove(this); 
			Population destination; 
			
			if (elsewhere.size() == 0) return moved;
			while(moved < toMove&& !wayfarers.isEmpty())
			{
				destination = elsewhere.get(Math.abs(rand.nextInt()%elsewhere.size()));
				traveler = wayfarers.get(Math.abs(rand.nextInt()%wayfarers.size()));
				if (traveler.move(destination))
				{
					this.leavePopulationLevelPools(traveler);
					this.populace.remove(traveler);
					this.males.remove(traveler);
					this.married_males.remove(traveler);
					this.married_females.remove(traveler);
					this.females.remove(traveler);
					wayfarers.remove(traveler);
					traveler.setResidence(destination);
					destination.addMember(traveler);
					traveler.setHasMoved(true);
					if (traveler.isMarried()) System.out.println("Error relocating married individual");
					if (traveler.getAge() >= this.fertilityEnd ) System.out.println("Error relocating infertile individual");
					moved++;
				}
			}
			return moved;
		}
	}
	/*
	* The individual comparitor class allows for lists of individuals to be sorted by age
	*/
	public static class Comparethem implements Comparator<Individual>
	{
		@Override
		public int compare(Individual i1, Individual i2)
		{
			return i1.getAge() - i2.getAge();
		}
	}

	/*
	* The individual class will represent one individual in the simulated population.  
	*/
	public static class Individual 
	{
		Random rn;
		private String name;  
		private String surname;  
		private boolean isDead = false;
		private Population residence; 
		private int gender;
		private ArrayList<Individual> siblings;
		private ArrayList<Individual> firstDegree_relatives;
		private ArrayList<Individual> secondDegree_relatives;
		private ArrayList<Individual> firstAndSecondDegree_relatives;
		private int age_secondDegree_calculated;
		private int age_firstDegree_calculated;
		private int age_firstAndSecondDegree_calculated;
		private Individual mother= null;
		private Individual father= null;
		private ArrayList<Individual>  children;
 		private Individual [] partners;    
		private int birthYear;
		private boolean hasMoved = false; 
		private int deathYear=-1;
		private int numChildren = 0 ;
		private int num_full_sibs = 0; 
		private int num_half_sibs = 0; 
		private Individual spouse;
		private	 boolean single = true; 
		private boolean fertile = false; 
		private boolean hadChild = false; 
		private boolean pregnant = false;
		private boolean immigrant  = false; 

    /*
    * Constructor for individual objects
    */
		public Individual(int gender,Population residence,int  birthYear )
		{
			siblings= new ArrayList<Individual>();
			children= new ArrayList<Individual>();
			firstDegree_relatives= new ArrayList<Individual>();
			secondDegree_relatives= new ArrayList<Individual>();
			firstAndSecondDegree_relatives= new ArrayList<Individual>();
			rn = new Random();
			this.gender = gender;
			this.residence = residence;
			this.birthYear = birthYear;
		}
	/*
	* Returns the individual's first name. 
	*/
		public String getName()
		{
			return this.name;
		}
	/*
	* Sets the individual's first name.  Called by the addMember() method. 
	*/
		public void setName(String name)
		{
			this.name = name;
		}
	/*
	* Sets the last name of an individual. 
	*/
		public void setSurname(String surname)
		{
			this.surname = surname;
		}
	/*
	* Returns the last name of an individual
	*/
		public String getSurname()
		{
			return this.surname;
		}
	/*
	* custom print method for Individual objects. 
	*/
		public String toString()
		{
			String gender = "f"; 
			if (this.gender == 1) gender = "m"; 
			String status = "single";
			if(!this.single) status = "married";
			if(this.isDead)  status = status + " and deceased";
			if(this.immigrant)  status = status + " immigrant ";
			return gender+this.getAge()+"_"+this.name+"_"+this.surname;
		}
	/*
	* Returns the gender of an individual
	*/
		public int getGender()
		{
			return this.gender;
		}
		public boolean getHasMoved()
		{
			return this.getHasMoved();
		}
		public void setHasMoved(boolean hasMoved)
		{
			this.hasMoved= hasMoved;
		}
	/*
	* Simulates the death of an Individual. Called by the cull() method. 
	*/
		public void passOn()
		{
      //System.out.println(this + " died");
			this.deathYear = this.residence.getYear();
			this.isDead = true;
			this.residence.region.departed.add(this);
		}
	/*
	* Returns true if the individual is deceased.
	*/
		public boolean isDead()
		{
			return this.isDead;
		}
		public boolean isMarried()
		{
			return !(this.spouse == null);
		}
	/*
	* Returns true if the individual is of reproductive age
	*/
		public boolean isFertile()
		{
			return (this.getAge() >= this.residence.getFertilityStart()&&this.getAge() < this.residence.getFertilityEnd()) ;
		}
	/*
	* Returns the age of the individual. 
	*/
		public int getAge()
		{
			return this.residence.getYear() - this.birthYear;
		}
	/*
	* Returns the birth year of the individual. 
	*/
		public int getBirthYear()
		{
			return this.birthYear;
		}
	/*
	* sets the birth year of the individual. 
	*/
		public void setBirthYear(int birthYear)
		{
			this.birthYear = birthYear;
		}
	/*
	* Returns the death year of the individual. If the individual is still alive it will return -1. 
	*/
		public int getDeathYear()
		{
			return this.deathYear;
		}
	/*
	* Return the population that the individual currently belongs to
	*/
		public Population getResidence()
		{
			return this.residence;
		}
	/*
	* Set the individual's residence
	*/
		public void setResidence(Population p)
		{	
			this.residence = p;
		}
	/*
	* Sets the immigration status of an individual 
	*/
		public void setImmigrant(boolean immigrant) 
		{
			this.immigrant = immigrant;
		}
	/*
	* Returns the immigration status of an individual
	*/
		public boolean getImmigrant()
		{
			return this.immigrant;
		}
	/*
	* Adds a new child to an individual's ArrayList of children.  Called by conceive() method 
	*/
		public void addChild(Individual child, boolean full_sib)
		{
			this.numChildren++;
			this.children.add(child);
			if (full_sib){this.num_full_sibs++;}
			else this.num_half_sibs++;
		}
	/*
	* Returns the number of full sib children an individual has had
	*/
		public int get_num_full_siblings()
		{
			return this.num_full_sibs;
		}
	/*
	* Returns the number of half_sibling children an individual has had 
	*/
		public int get_num_half_siblings()
		{
			return this.num_half_sibs;
		}
	
	/*
	* Sets the mother of an Indivvidual. Called by the conceive() method
	*/
		public void setMother(Individual mother)
		{
			this.mother = mother;
		}

	/*
	* Sets the father of an individual. Called by the conceive method()
	*/
		public void setFather(Individual father)
		{
			this.father = father;  
		}
	/*
	* Returns the father of an individual, will return null if the father is not defined
	*/
		public Individual getFather()
		{
			
			if (this.father != null)return this.father;
			else return null;
		}
	/*
	* Sets the spouse of an individual. Called by the population court() method
	*/
		public void setSpouse(Individual spouse)
		{
			this.spouse = spouse;
			this.single = false;  
		}
	/*
	* Returns the Spouse of an individual, will return null if the individual is single
	*/
		public Individual getSpouse()
		{
			return this.spouse;
		}
	/*
	* Returns the mother of the individual, will return null if the mother is not defined 
	*/
		public Individual getMother()
		{
			if (this.mother != null)return this.mother;
			else return null;
		}
	/*
	* Returns an individuals children, will return an empty array list if the individual has no children
	*/
		public ArrayList<Individual>  getChildren()
		{
			return this.children;
		}

	/*
	* Returns a list of second degree relatives 
	*/
		public ArrayList<Individual> getSecondDegreeRelatives()
		{
			ArrayList<Individual> relatives = new ArrayList<Individual>();
			ArrayList<Individual> temp = new ArrayList<Individual>();
			HashSet<Individual> unique = new HashSet<Individual>();
			ArrayList<Individual> maternal_siblings= new ArrayList<Individual>();
			ArrayList<Individual> paternal_siblings = new ArrayList<Individual>();

      if (this.getAge() == this.age_secondDegree_calculated)
      {
        //System.out.println("HERE1 " + this +"\n");
        return this.secondDegree_relatives;
      }

			relatives.addAll(this.getRelatives(1));
      for (Individual r : relatives)
      {
        // mother
        if (!r.equals(null) && r.equals(this.mother))
          {
          if (r.getMother()!= null) temp.add(r.getMother());
          if (r.getFather()!= null) temp.add(r.getFather());
          temp.addAll(r.getFullSiblings());
          temp.addAll(this.getMaternalSiblings());
          }
        //father
        if (!r.equals(null) && r.equals(this.father))
          {
          if (r.getMother()!= null) temp.add(r.getMother());
          if (r.getFather()!= null) temp.add(r.getFather());
          temp.addAll(r.getFullSiblings());
          temp.addAll(this.getPaternalSiblings());
          }
        //children
        if (this.getChildren().contains(r)) temp.addAll(r.getChildren());
        // full siblings
        if( this.getFullSiblings().contains(r)) temp.addAll(r.getChildren()); 
      }
			unique.addAll(temp);
			unique.remove(null);
			relatives.clear();
			relatives.addAll(unique);
			relatives.remove(this);

      this.secondDegree_relatives.addAll(relatives);
      this.age_secondDegree_calculated = this.getAge();

			return relatives; 
		}

	/*
	* Returns the relatives of an individual to the given degree inclusive of all lower degrees.  
	*/
		public ArrayList<Individual> getRelatives(int degree)
		{
			ArrayList<Individual> relatives = new ArrayList<Individual>();
			ArrayList<Individual> temp = new ArrayList<Individual>();
			HashSet<Individual> unique = new HashSet<Individual>();
			ArrayList<Individual> maternal_siblings= new ArrayList<Individual>();
			ArrayList<Individual> paternal_siblings = new ArrayList<Individual>();

			//relatives.add(this);
			if (degree < 1)degree = 1;
			if (degree > 3)degree= 3;
			if (degree == 1)
			{
        // Don't recaculcate relationships if already calculated at this age
        if (this.getAge() == this.age_firstDegree_calculated)
        {
          //System.out.println("HERE1 " + this +" rel1: "+ this.firstDegree_relatives + "\n");
          return this.firstDegree_relatives;
        }
        //System.out.println("HERE0 " + this +"\n");
        this.firstDegree_relatives.clear();

        // Get fir st degree relatives
				if (this.mother != null) 
				{
					relatives.add(this.mother);
				}
				if (this.father != null)
				{
					relatives.add(this.father);
				}
				relatives.addAll(this.getFullSiblings());
				relatives.addAll(this.children);
        //System.out.println(this + " children: " + this.children + "relatives: " + relatives);
        this.firstDegree_relatives.addAll(relatives);
        this.age_firstDegree_calculated = this.getAge();
        //System.out.println("HERE0 " + this +" rel1: "+ this.firstDegree_relatives + "\n");
        return relatives;
			}
			else if (degree == 2)
			{
        if (this.getAge() == this.age_firstAndSecondDegree_calculated)
        {
          return this.firstAndSecondDegree_relatives;
        }
       this.firstAndSecondDegree_relatives.clear();

        temp.addAll(this.getRelatives(1));
				//relatives.addAll(this.getRelatives(1));
				relatives.addAll(temp);
				
				for (Individual r : relatives)
				{
					// mother
					if (!r.equals(null) && r.equals(this.mother))
					{
						if (r.getMother()!= null) temp.add(r.getMother());
						if (r.getFather()!= null) temp.add(r.getFather());
						temp.addAll(r.getFullSiblings());
						temp.addAll(this.getMaternalSiblings());
					}
					//father
					if (!r.equals(null) && r.equals(this.father))
					{
						if (r.getMother()!= null) temp.add(r.getMother());
						if (r.getFather()!= null) temp.add(r.getFather());
						temp.addAll(r.getFullSiblings());
						temp.addAll(this.getPaternalSiblings());
					}
					//children
					if (this.getChildren().contains(r)) temp.addAll(r.getChildren());

					// full siblings
					if( this.getFullSiblings().contains(r)) temp.addAll(r.getChildren()); 
				}
				relatives.addAll(temp);

        unique.addAll(temp);	
        unique.remove(null);
        unique.remove(this);
        relatives.clear();
        relatives.addAll(unique);
        this.firstAndSecondDegree_relatives.addAll(relatives);
        this.age_firstAndSecondDegree_calculated = this.getAge();
        return relatives;
			}
			else if (degree == 3)
			{
				//relatives.addAll(this.getRelatives(1));
				temp.addAll(this.getRelatives(2));
				//relatives.addAll(this.getRelatives(2));
				relatives.addAll(temp);
				
				for (Individual r : relatives)
				{
					//full siblings
					if (this.getFullSiblings().contains(r))
					{
						for (Individual a : r.getChildren())
							temp.addAll(a.getChildren());
					}
					// half siblings
					if (this.getMaternalSiblings().contains(r)
					|| this.getPaternalSiblings().contains(r))
					{
						temp.addAll(r.getChildren());
					}
					//children
					if (this.children.contains(r))
					{
						for (Individual i :r.getChildren())
							temp.addAll(i.getChildren());			
					}
					//parents
					if (r.equals(this.father)|| r.equals(this.mother))
					{
						temp.addAll(r.getPaternalSiblings());
						temp.addAll(r.getMaternalSiblings());
					}
					// aunts + uncles
					if (this.mother !=  null)
					{
						if(this.mother.getFullSiblings().contains(r))
						{
							temp.addAll(r.getChildren());
						}
					}
					if (this.father !=  null)
					{
						if(this.father.getFullSiblings().contains(r))
						{
							temp.addAll(r.getChildren());
						}
					}
					//grandparents
					for (Individual p : r.getChildren())
					{
						if (p.getChildren().contains(this))
						{
							temp.addAll(r.getFullSiblings());
							if (r.getFather() != null) 
								temp.add(r.getFather());
							if (r.getMother() != null) 
								temp.add(r.getMother());
						}
					}
				}
	
        unique.addAll(temp);	
        unique.remove(null);
        unique.remove(this);
        relatives.clear();
        relatives.addAll(unique);
        return relatives;
			}
      else
      {
        return relatives;
      }
      //unique.addAll(relatives);	
			//unique.remove(null);
			//relatives.clear();
			//relatives.addAll(unique);
			//relatives.remove(this);
			//return relatives; 
		}
	/*
	* Returns all half siblings from only the mother's side
	*/
		public ArrayList<Individual> getMaternalSiblings()
			{
				ArrayList<Individual> maternalSiblings = new ArrayList<Individual>();
				if (this.mother == null) return maternalSiblings;
				maternalSiblings.addAll(this.mother.getChildren());
				maternalSiblings.remove(this);
				maternalSiblings.removeAll(this.getFullSiblings());
				return maternalSiblings;
			}
	/*
	* Returns all  half siblings from only the father's side
	*/
		public ArrayList<Individual> getPaternalSiblings()
			{
				ArrayList<Individual> paternalSiblings = new ArrayList<Individual>();
				if (this.father == null) return paternalSiblings;
				paternalSiblings.addAll(this.father.getChildren());
				paternalSiblings.remove(this);
				paternalSiblings.removeAll(this.getFullSiblings());
				return paternalSiblings;
			}
	/*
	* Returns all full siblings
	*/
		public ArrayList<Individual> getFullSiblings()
			{
				ArrayList<Individual> paternalSiblings = new ArrayList<Individual>();
				ArrayList<Individual> maternalSiblings = new ArrayList<Individual>();
				if (this.father == null) return paternalSiblings;
				if (this.mother == null) return maternalSiblings;
				paternalSiblings.addAll(this.father.getChildren());
				maternalSiblings.addAll(this.mother.getChildren());
				maternalSiblings.remove(this);
				paternalSiblings.remove(this);
				paternalSiblings.retainAll(maternalSiblings);
				return paternalSiblings;
			}


    /*
    * Determines if an individual is related to the given other by at most the given degree. 
    */
		public boolean isRelated(Individual other, int degree)
		{
			ArrayList<Individual> others_relatives= new ArrayList<Individual>();
			ArrayList<Individual>  temp_rel= new ArrayList<Individual>(); 
			for (int d = 1; d <= degree; d++)
			{
				temp_rel.addAll(this.getRelatives(d));
				others_relatives.addAll(other.getRelatives(d));  
			}
				other.getRelatives(degree);
			temp_rel.retainAll(others_relatives); 
			if (temp_rel.size() >  0) return true;
			return false;
		}

    /*
    * Simulates a mother conceiving a child. Called by the mingle() method. 
    */
		public int conceive(Individual mate, boolean full_sibling)
		{
			int numChildren = 1;
			int multiple = Math.abs(rn.nextInt());
			if (multiple%30==0){numChildren = 2;}
			if (multiple%1000==1){numChildren = 3;}
			for (int o  = 0; o  < numChildren; o++)
			{ 
				Individual child = new Individual(
							Math.abs(rn.nextInt()%2),
							this.residence, 
							this.residence.getYear() );
				child.setSurname(mate.getSurname());
				this.residence.addMember(child);
				child.setMother(this);
				child.setFather(mate);
				this.residence.getRegion().addJuvenile(child);
				mate.addChild(child, full_sibling);
				this.addChild(child, full_sibling);
			}
			return numChildren; 
		}
			
	/*
	* Attempts to make the individual reproduce with the given mate. Returns false if any of the disqualifying conditions are met. 
	*/
		public boolean mate(Individual mate)
		{
			double token = rn.nextDouble();
			if (!this.isFertile())
      {
        System.out.println("Is in fertile? " + this.getResidence().region.matingPool.contains(this));
        System.out.println("Is in aged? " + this.getResidence().region.agedPool.contains(this));
        System.out.println("Is in married_female? " + this.getResidence().married_females.contains(this));
        System.out.println("Mating Failure ("+this+" " + this.immigrant +" " + this.isDead() +") : infertile");return false;
      }
		  if (this.gender == 0 && token > this.residence.getFertility(this.getAge())) return false;
			if (this.single && this.isRelated(mate, 3))return false;
			return true;
		}

	/*
	* Attempts to marry the individual to the given spouse. Returns false if any disqualifying condiions are met; otherwise marries them and returns true
	*/
		public boolean  marry(Individual wife)
		{
			if(this.isMarried())
      {
        System.out.println("Marrige Failure : ("+this+") already married "+this.immigrant);
        return false; 
      } 
			if (!this.isFertile())
			{ 
        System.out.println("Marriage Failure: ("+this+") infertile "+this.immigrant);
        return false;
      }
			if(wife.isMarried())
      {
        System.out.println("Marrige Failure : ("+wife+") already married "+wife.immigrant);
        return false; 
      } 
			if (!wife.isFertile())
			{ 
        System.out.println("Marriage Failure: ("+wife+") infertile "+wife.immigrant);
        return false;
      }
			if (this.isRelated(wife, 3))
      {
        return false;//{System.out.println("Marriage Failure: too closely related");return false;}
      }

      wife.getResidence().females.remove(wife);
      wife.getResidence().married_females.add(wife);
      this.getResidence().males.remove(this);
      this.getResidence().married_males.add(this);
      wife.setSpouse(this);
      this.setSpouse(wife);
      wife.setSurname(this.getSurname());

			return true;
		}
	/*
	* Attempts to move an individual to a new location. 
	*/
		public boolean move(Population p)
		{
			//if (!this.isFertile()){System.out.println("Relocation failure, traveler not fertile"); return false;} 
			if (this.isMarried()){System.out.println("Relocation failure, traveler not single"); return false;} 
			return true; 
		}
	}


//////////    REGION CLASS   /////////////////////

	/*
	* Simulates a collection of poulations that may have different geographic locations. 
	*/
	public static class Region
	{
		int year = 1800; 
		int startYear = 1800;
		Random rand = new Random();
		private ArrayList<Population> pops;
		private ArrayList<String> city_names;
		private ArrayList<Individual> juvenilesPool; // SHOULD RENAME TO juvenilesPool
		private ArrayList<Individual> matingPool;
		private ArrayList<Individual> agedPool;
		private ArrayList<Individual> departed;

    /*
    * Constructor for Region objects
    */
		public Region(ArrayList<String> city_names)
		{
			pops = new ArrayList<Population>();
			juvenilesPool = new ArrayList<Individual>();
			matingPool = new ArrayList<Individual>();
			agedPool = new ArrayList<Individual>();
			departed = new ArrayList<Individual>();
			this.city_names= city_names; 
		}

    /*
    * Returns the ArrayList of individuals that form the deceased population
    */
		public ArrayList<Individual>  getDeparted()
		{
			return this.departed;	
		}

    public void fix_duplicate_names()
    {
      System.out.println("Fixing duplicate names...");
      //ArrayList<String> used_names = new ArrayList<String>();
			
      ArrayList<Individual> to_traverse = new ArrayList<Individual>(this.getPopulace());
			HashMap<String, Integer> used_names = new HashMap<String, Integer>();

      to_traverse.addAll(this.departed);

			for (Individual i : to_traverse)
      {
        String name = i.toString();
        //System.out.println("size: " + name + " " + used_names.size());
        while(used_names.containsKey(name))
        {
          //System.out.println("Duplicate name: " + name);
			    int nameIndex = Math.abs(rand.nextInt());
          if (i.getGender() == 0)
          {
            i.setName(i.residence.female_names.get(nameIndex%i.residence.female_names.size()));
          }
          else 
          {
            i.setName(i.residence.male_names.get(nameIndex%i.residence.male_names.size()));
          }
          name = i.toString();
          //System.out.println("new name: " + name);
        }
        used_names.put(name,1);
      }
    }

    public void exportPed(String filename) throws IOException
    {
      String gender = "3";
      String father = "0";
      String mother = "0";
    
      
			ArrayList<Individual> samples_to_export = new ArrayList<Individual>();
      ArrayList<Individual> samples_exported = new ArrayList<Individual>();


      //	w.println("FamilyID\tIndividualID\tPaternalID\tMaternalID\tSex\tPhenotype\tAge");
      ArrayList<String> popnames = new ArrayList<String>();
		  PrintWriter w = new PrintWriter(filename, "UTF-8");
      for (Population p : this.pops)
      {
        samples_to_export.clear();
        System.out.println("PED FILE INFO");  
        System.out.println(p.toString());

        samples_to_export.addAll(p.populace);

        int ctr = 0;
        while( ctr < samples_to_export.size())
        {
          Individual i = samples_to_export.get(ctr);
          if(samples_exported.contains(i)) 
          {
            ctr++;
            continue;
          }
          gender = "3";
          father = "0";
          mother = "0";
          int isDead = 0;
          if(i.isDead())isDead = 1;
          
          if(isDead == 1)
          {
            if(!this.departed.contains(i))
            {
              System.out.println(i + " is dead but not in departed: ");
            }
          }

          if(i.getMother() != null) mother = i.getMother().toString();
          if(i.getFather() != null) father = i.getFather().toString();
          if (i.getGender() == 0) gender = "2"; else gender = "1";
          w.println("Fam"+"\t"+i+"\t"+father+"\t"+mother+"\t"+gender+"\t"+isDead+"\t"+i.getAge()+"\t"+p.getName());
          samples_exported.add(i);
          if(i.getMother() != null && !samples_exported.contains(i.getMother()))
          {
            //System.out.println("Dead mother: " + mother);
            samples_to_export.add(i.getMother());
          }
          if(i.getFather() != null && !samples_exported.contains(i.getFather()))
          {
            //System.out.println("Dead father: " + father);
            samples_to_export.add(i.getFather());
          }
          ctr++;
        }
      }
      w.close();
    }	



    /* 
    * add new population to the region
    */
		public void addPop(Population pop)
		{
			if(pop.getName() =="") pop.setName(city_names.get(Math.abs(rand.nextInt()%city_names.size())));
			pops.add(pop);
			juvenilesPool.addAll(pop.getJuveniles());
			matingPool.addAll(pop.getMatingAge());
			agedPool.addAll(pop.getAged());

      this.sortPools();
      /*
      System.out.println("juvenile Size: " + this.juvenilesPool.size()); 
      System.out.println("matingPool Size: " + this.matingPool.size()); 
      System.out.println("agedPool Size: " + this.agedPool.size()); 
      for (int i = this.agedPool.size()-1; i >= 0; i--)
			{
				if(i < this.agedPool.size()-10)
        {
          break;
        }
        Individual ad = this.agedPool.get(i);
        System.out.println("agedPool high after: "+ ad + " " + ad.getAge() + " " + ad.isDead());
			}
			*/

		}

    /*
    * Clears the region so a new simulation may begin
    */
		public void clear()
		{
			year = 1800; 
			pops = new ArrayList<Population>();
			juvenilesPool = new ArrayList<Individual>();
			matingPool = new ArrayList<Individual>();
			agedPool = new ArrayList<Individual>();
		}

    /*
    * Sort the regions juvenile, mating, and aged pools by age
    */
		public void sortPools()
		{
			Collections.sort(this.juvenilesPool, new Comparethem());
			Collections.sort(this.matingPool, new Comparethem());
			Collections.sort(this.agedPool, new Comparethem());
		}

    /*
    * Adds a newborn to the juvenile list
    */
		public void addJuvenile(Individual i ) 
		{
			this.juvenilesPool.add(0,i);
		}

    /* 
    * Adds a juvenile aged individual to the Region level juveniles pool in a specific location to maintain the sorted age order
    */ 
		public void addJuvenileToRegionLevelSortedJuvenilePool(Individual i , int index) 
		{
			this.juvenilesPool.add(index,i);
			int nameIndex = Math.abs(rand.nextInt());
			if (i.getGender() == 0)
			{
				if (i.getName() == null)i.setName(i.residence.female_names.get(nameIndex%i.residence.female_names.size()));
			}
			else 
			{
				if (i.getName() == null)i.setName(i.residence.male_names.get(nameIndex%i.residence.male_names.size()));
			}
			int surnameIndex = Math.abs(rand.nextInt());
			if (i.getSurname() == null)i.setSurname(i.residence.last_names.get(surnameIndex%i.residence.last_names.size()));
		}

    /* 
    * Adds a mating age individual to the Region level mating pool in a specific location to maintain the sorted age order
    */ 
		public void addAdultToRegionLevelSortedMatingPool(Individual i , int index) 
		{
			this.matingPool.add(index,i);
			int nameIndex = Math.abs(rand.nextInt());
			if (i.getGender() == 0)
			{
				if (i.getName() == null)i.setName(i.residence.female_names.get(nameIndex%i.residence.female_names.size()));
			}
			else 
			{
				if (i.getName() == null)i.setName(i.residence.male_names.get(nameIndex%i.residence.male_names.size()));
			}
			int surnameIndex = Math.abs(rand.nextInt());
			if (i.getSurname() == null)i.setSurname(i.residence.last_names.get(surnameIndex%i.residence.last_names.size()));
		}

    /* 
    * Adds a aged individual to the Region level aged pool in a specific location to maintain the sorted age order
    */ 
		public void addAdultToRegionLevelSortedAgedPool(Individual i , int index) 
		{
			this.agedPool.add(index,i);
			int nameIndex = Math.abs(rand.nextInt());
			if (i.getGender() == 0)
			{
				if (i.getName() == null)i.setName(i.residence.female_names.get(nameIndex%i.residence.female_names.size()));
			}
			else 
			{
				if (i.getName() == null)i.setName(i.residence.male_names.get(nameIndex%i.residence.male_names.size()));
			}
			int surnameIndex = Math.abs(rand.nextInt());
			if (i.getSurname() == null)i.setSurname(i.residence.last_names.get(surnameIndex%i.residence.last_names.size()));
		}

    /*
    * Returns the region's master juvenile list
    */
		public ArrayList<Individual> getJuvenilesPool()
		{
			return this.juvenilesPool; 
		}

    /*
    * Returns the region's master mating pool list
    */
		public ArrayList<Individual> getMatingPool()
		{
			return this.matingPool; 
		}

		public ArrayList<Individual> getAgedPool()
		{
			return this.agedPool; 
		}

    /*
    * Move the region population through time without changing the size
    */
		public void burnIn(int duration)
		{
			for (int i = 0;  i < duration;i++) 
				this.elapseRegionYear(true, 10); 
		}

    /*
    * Simulate the passage of one year in the region. 
    */
		public void elapseRegionYear(boolean keepStatic, int survey )
		{
			long start = System.nanoTime();
			System.out.println("----- Year: "+ year+" -----");
			long sort = System.nanoTime(); 
			for (Population p : pops) p.elapseYear(keepStatic, survey);
      
      // Update the pools based on age
			for (int j = this.juvenilesPool.size()-1 ;j >= 0; j--)
			{
				Individual juv = this.juvenilesPool.get(j);
				if (juv.isFertile())
				{
          //System.out.println("juv size before: " + juv.getResidence().juveniles.size());
					juv.getResidence().joinPopulationLevelPools(juv);
          //System.out.println("juv size after: " + juv.getResidence().juveniles.size());
					this.juvenilesPool.remove(juv);
					this.matingPool.add(0,juv);
				}
				else break;
			}
			for (int i = this.matingPool.size()-1 ; i >= 0; i--)
			{
				Individual ad = this.matingPool.get(i);
				if (ad.getAge() >= (ad.getResidence().getFertilityEnd()-1) )
				{
          //System.out.println("Removing " + ad + " " + ad.getAge());
					ad.getResidence().leavePopulationLevelPools(ad);
					this.matingPool.remove(ad);
          this.agedPool.add(0,ad);
				}
				else break;
			}
			year++;

			long finish = System.nanoTime();
			double totalTime = (double)(finish - start)/1000000000;
			double sortTime = (double)(sort - start)/1000000000; 
			double elapseTime = (double)(finish - sort)/1000000000;
			System.out.println("Total time: "+ totalTime);
			System.out.println("Total Population: "+this.getSize());  
			//System.out.println("Time to elapse populations: "+elapseTime);
			//System.out.println("Time to sort master lists: "+sortTime);
			//System.out.println("Total time: "+totalTime);
		}

    /*
    * Returns the region's list of populations 
    */
		public ArrayList<Population> getPops()
		{
			return this.pops;
		}

    /*
    * Return an array of strings containing the populations names
    */
		public ArrayList<String> getNames()
		{
			ArrayList<String> popnames = new ArrayList<String>();
			for (Population p : this.pops) popnames.add(p.getName());
			return popnames;
		}

    /*
    * Returns the collective population of the region. 
    */
		public ArrayList<Individual> getPopulace()
		{
			ArrayList<Individual> populace  = new ArrayList<Individual>();
			for (Population p : this.getPops()) populace.addAll(p.getPopulace());
			return populace; 
		}

    /*
    * Return the current year. 
    */
		public int getYear()
		{
			return this.year;
		}

    /*
    * Returns the total population of the region
    */ 	
		public int  getSize()
		{
			int total = 0; 
			for (Population p: this.getPops())total+= p.getSize();
			return total; 
		}

    /*
    * Return a random sample of a set size accross all populations
    */
		public ArrayList<Individual> randomSample(ArrayList<Individual> pool, int sampleSize)
		{
			ArrayList<Individual> sample = new ArrayList<Individual>();
			while (sample.size() < sampleSize && !pool.isEmpty())
			{
				Individual chosen = pool.get(Math.abs(rand.nextInt()%pool.size()));
				sample.add(chosen);
				pool.remove(chosen);
			}
			return sample; 
		}

    /*
    * Return a sample based on the Geisinger ascertainment method
    */
		public ArrayList<Individual> clusteredSample(ArrayList<Individual> pool, int sampleSize)
		{
			Population location;
			Individual chosen;
			int toGrab;

			List<Individual> grabbed = new ArrayList<Individual>();
			List<Individual> grabbed2 = new ArrayList<Individual>();
			ArrayList<Individual> deg1;
			ArrayList<Individual> deg2;
			ArrayList<Individual>  sample = new ArrayList<Individual>();
			while (sample.size() < sampleSize && !pool.isEmpty())
			{
				grabbed.clear();
				grabbed2.clear();
        if (sample.size()%1000 == 0 )  System.out.println("Sample size: " + sample.size()); 
				chosen = pool.get(Math.abs(rand.nextInt()%pool.size()));
				sample.add(chosen);
				pool.remove(chosen);

        int firstDegreeToGrab = getPoissonRandom(config.lambda1stDegreeClusteredAscertainment);
        int secondDegreeToGrab = getPoissonRandom(config.lambda2ndDegreeClusteredAscertainment);
        //System.out.println(chosen + " to grab: " + firstDegreeToGrab + " " + secondDegreeToGrab);

        deg1 = chosen.getRelatives(1);
        //System.out.println("deg1: " + deg1);
        Collections.shuffle(deg1);
        if (deg1.size() < firstDegreeToGrab) firstDegreeToGrab = deg1.size();
        grabbed = deg1.subList(0, firstDegreeToGrab);
        //System.out.println("grabbed 1st degree: "+ grabbed.size()); 
        //System.out.println("grabbed deg1 list: "+ grabbed); 

        deg2 = chosen.getSecondDegreeRelatives();
        Collections.shuffle(deg2);
        if (deg2.size() < secondDegreeToGrab) secondDegreeToGrab = deg2.size();
        grabbed2 = deg2.subList(0, secondDegreeToGrab);
        //System.out.println("deg2: " + deg2);
        //System.out.println("grabbed deg2 list: "+ grabbed2);

        /*
				Collections.shuffle(deg1);
				toGrab = (int) Math.round((double)deg1.size()*config.lambda1stDegreeClusteredAscertainment);
				//System.out.println(grabbed);
				if (toGrab < 0) toGrab = 0;
				grabbed = deg1.subList(0, toGrab);

        deg2 = chosen.getSecondDegreeRelatives();
        Collections.shuffle(deg2);

        toGrab = (int) Math.round((double)deg2.size()*config.lambda2ndDegreeClusteredAscertainment);
        end  =  toGrab; 
        if (end < 0) end = 0;
        grabbed2 = deg2.subList(0, end);
        */
        //System.out.println("Grabbed1: " + grabbed);
        grabbed.addAll(grabbed2);
        //System.out.println("Grabbed1+2: " + grabbed);
				
        for(Individual i : grabbed)
        {
				  if(pool.remove(i))
          {
            sample.add(i);
          }
        }
        //sample.addAll(grabbed);
        //System.out.println("sample: " + sample + "\n");
        
				//System.out.println("Individuals gathered so far: " + sample.size());
			}
      //System.exit(1);
			return sample;
		}

    private static int getPoissonRandom(double mean) 
    {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }

    public ArrayList<Individual> ascertain(String outfile_root) throws IOException
    {
      int clustered = 0;
      int sample_size = 0;

			ArrayList<Individual> grabbed = new ArrayList<Individual>();
			ArrayList<Individual> ordered_group = new ArrayList<Individual>();
      ArrayList<Individual> temp_grabbed = new ArrayList<Individual>();
      
      
      PrintWriter write = new PrintWriter(outfile_root + ".populace.txt", "UTF-8");
      for(Individual i : this.getPopulace())
      {
        write.println(i);
      }
      write.close();



      // Check if clustered
      if(config.ascertainment_approach.equals("clustered"))
      {
        clustered = 1;
      }
      else if(!config.ascertainment_approach.equals("random"))
      {
        System.out.println(" Invalid ascertainment approach: " + config.ascertainment_approach + ".\n Performing Random ascertainment instead.");
      }

      // Set order values
      int order_ctr = 0;
      int max_ctr = 1;
      int region_size = 0;
      for (Population p : this.getPops())
      {
        region_size += p.getSize();
        if(p.get_ascertainment_order() > max_ctr)
        {
          max_ctr = p.get_ascertainment_order();
        }
        if(p.get_ascertainment_order() < order_ctr || order_ctr == 0)
        {
          order_ctr = p.get_ascertainment_order();
        }
      }

      // Open ascertainmemt file
      write = new PrintWriter(outfile_root + ".ascertainment_order", "UTF-8");

      // Iterate over the ordered list of populations and sample from them accordingly
      for(int i = 0; i <= max_ctr; i++)
      {
        
        // Set pool
        ordered_group.clear();
        for (Population p : this.getPops())
        {
          if(p.get_ascertainment_order() == i)
          {
            ordered_group.addAll(p.getPopulace());
          }
        }
        
        if(ordered_group.size() < 1)
        {
          continue;
        }

        System.out.println("Processing order pool " + i);
        // Sample from pool
        sample_size = (int)Math.round((double)ordered_group.size() * config.ordered_sampling_proportion);
        temp_grabbed.clear();
        if(clustered == 1)
        {
          temp_grabbed = clusteredSample(ordered_group,sample_size);
        }
        else
        {
          temp_grabbed = randomSample(ordered_group,sample_size);
        }
        
        
        // write out grabbed to file
        for(Individual ind : temp_grabbed)
        {
          write.println(ind+"\t"+i);
        }

        // Add to overal group
        grabbed.addAll(temp_grabbed);
      }

      // Sample from entire region until 100% of the Region is ascertained
      System.out.println("Sampling from all populations");
      sample_size = (int)Math.round((double)region_size * 0.5) - grabbed.size();
      //sample_size = 270000 - grabbed.size();
      //sample_size = 100000 - grabbed.size();
      //sample_size = 27000 - grabbed.size();
      ordered_group.clear();
      ordered_group.addAll(this.getPopulace());
      ordered_group.removeAll(grabbed);
      temp_grabbed.clear();
      if(clustered == 1)
      {
        temp_grabbed = clusteredSample(ordered_group,sample_size);
      }
      else
      {
        temp_grabbed = randomSample(ordered_group,sample_size);
      }
      
      // write out grabbed to file
      for(Individual ind : temp_grabbed)
      {
        write.println(ind+"\t-1");
      }

      // Add to overal group
      grabbed.addAll(temp_grabbed);
      System.out.println("grabbed after all: " + grabbed.size());
      
      write.close();

      return grabbed;
    }

    

    /*
    * Return a sample where individuals are drawn from populations in a definite order 
    */
		public ArrayList<Individual> orderedSample(int sampleSize, boolean cluster, int num_sampled_from_population, int num_populations_before_random, double lambda1stDegreeClusteredAscertainment, double lambda2ndDegreeClusteredAscertainment) throws IOException
		{
			Population location;
			Individual chosen;
			int zipcode = 0;
			int trickle = 10;
			int taken = 0;
      int populations_sampled_in_order = 0;
			int toGrab;

      int tracking_increments = 200;
      int increment_ctr = 0;

      PrintWriter write = new PrintWriter("Ascertaiment_ordered_"+ num_populations_before_random +".clustered_"+ cluster +".sample" + sampleSize + ".fromPop" + num_sampled_from_population + "x"+ num_populations_before_random +".txt", "UTF-8");
      write.println("Method,SampleSize,Deg1,Deg2,Deg3,totDeg1,totDeg2,totDeg3");

			List<Individual> grabbed = new ArrayList<Individual>();
			List<Individual> grabbed2 = new ArrayList<Individual>();
			ArrayList<Individual> deg1;
			ArrayList<Individual> deg2;
			HashMap<Population, Integer> tally = new HashMap<Population, Integer>();
			for(Population p : this.pops)tally.put(p , 0);
      System.out.println("num to order sample from a population: " + num_sampled_from_population);
      System.out.println("pops: " + this.pops);
			ArrayList<Individual> sample = new ArrayList<Individual>();
			ArrayList<Individual> pool = new ArrayList<Individual>();
			pool.addAll(this.getPopulace());
			while (sample.size() < sampleSize && !pool.isEmpty() && populations_sampled_in_order < num_populations_before_random)
			{
				if (zipcode >= this.pops.size()) zipcode = 0;
				location  = this.pops.get(zipcode);
				while((tally.get(location)  < num_sampled_from_population || taken < trickle) && !pool.isEmpty())
				{
          populations_sampled_in_order = populations_sampled_in_order + 1;

          /*
				  if ((int) Math.round(sample.size() / tracking_increments) > increment_ctr) 
          {
            increment_ctr = (int) Math.round(sample.size()/tracking_increments);
            System.out.println("location: " + location.getName() + " (" + tally.get(location) + ")");
            String sampleInfo = analyze(sample,0); 
          }
          */
					if(sample.size() >= sampleSize) 
					{
						System.out.println("sample size reached "+ sample.size()); 
						break;
					}
					chosen = location.getPopulace().get(Math.abs(rand.nextInt()%location.getSize()));
					if (pool.contains(chosen) ) 
					{
            sample.add(chosen);
            pool.remove(chosen); 	
            tally.put(location,  tally.get(location) +1); 
            taken++;

						if (cluster)
						{
              //RandomEngine engine = new DRand();
              //Poisson poisson = new Poisson(4, engine);
              //int poissonObs = poisson.nextInt();

              //System.out.println("poisson val: " + poissonObs);
              
              int firstDegreeToGrab = getPoissonRandom(lambda1stDegreeClusteredAscertainment);
              int secondDegreeToGrab = getPoissonRandom(lambda2ndDegreeClusteredAscertainment);
              //System.out.println("first degree to grab: " + firstDegreeToGrab);
              //System.out.println("second degree to grab: " + firstDegreeToGrab);

							deg1 = chosen.getRelatives(1);
							if (deg1.size() < firstDegreeToGrab) firstDegreeToGrab = deg1.size();

				      Collections.shuffle(deg1);
							grabbed = deg1.subList(0, firstDegreeToGrab);
						  //System.out.println("grabbed 1st degree: "+ grabbed.size()); 
						  //System.out.println("grabbed deg1 list: "+ grabbed); 

              deg2 = chosen.getSecondDegreeRelatives();
							if (deg2.size() < secondDegreeToGrab) secondDegreeToGrab = deg2.size();
				      Collections.shuffle(deg2);

							grabbed2 = deg2.subList(0, secondDegreeToGrab);
						  //System.out.println("grabbed 2nd degree: "+ grabbed2.size()); 
						  //System.out.println("grabbed2 list: "+ grabbed2); 
							grabbed.addAll(grabbed2);
						  //System.out.println("grabbed 1st + 2nd degree: "+ grabbed.size()); 
						  //System.out.println("grabbed 1st + 2nd list: "+ grabbed); 
              sample.addAll(grabbed);
							pool.removeAll(grabbed);
							taken += (grabbed.size()); 
							tally.put(location, tally.get(location) + (grabbed.size())); 
						}
					} 
				}
				zipcode++; 
				taken = 0; 
			}

      // In case I need to ascertain beyond the number of order populations from which I want to sample
      while (sample.size() < sampleSize && !pool.isEmpty() )
      {
        /*
        if ((int) Math.round(sample.size()/tracking_increments) > increment_ctr) 
        {
          increment_ctr = (int) Math.round(sample.size()/tracking_increments);
          System.out.println("Sample size: " + sample.size());
          String sampleInfo = analyze(sample,0); 
        }
        */
				chosen = pool.get(Math.abs(rand.nextInt()%pool.size()));
				sample.add(chosen);
        tally.put(chosen.getResidence(),  tally.get(chosen.getResidence()) +1); 
				pool.remove(chosen);

        if(cluster)
        {
          deg1 = chosen.getRelatives(1);
          Collections.shuffle(deg1); 
          int firstDegreeToGrab = getPoissonRandom(lambda1stDegreeClusteredAscertainment);
          int secondDegreeToGrab = getPoissonRandom(lambda2ndDegreeClusteredAscertainment);
          //System.out.println("first degree to grab: " + firstDegreeToGrab);
          //System.out.println("second degree to grab: " + firstDegreeToGrab);

          deg1 = chosen.getRelatives(1);
          if (deg1.size() < firstDegreeToGrab) firstDegreeToGrab = deg1.size();

          Collections.shuffle(deg1);
          grabbed = deg1.subList(0, firstDegreeToGrab);
          //System.out.println("grabbed 1st degree: "+ grabbed.size()); 
          //System.out.println("grabbed deg1 list: "+ grabbed); 

          deg2 = chosen.getSecondDegreeRelatives();
          if (deg2.size() < secondDegreeToGrab) secondDegreeToGrab = deg2.size();
          Collections.shuffle(deg2);

          grabbed2 = deg2.subList(0, secondDegreeToGrab);
          grabbed.addAll(grabbed2);
          sample.addAll(grabbed);
          tally.put(chosen.getResidence(),  tally.get(chosen.getResidence()) +1); 
          pool.removeAll(grabbed);
        }
      }
			for (Population pop : this.pops)
			{
				double percent = (double)tally.get(pop)/(double)pop.getSize(); 
				System.out.println(pop.getName() +": " +tally.get(pop)+ "("+Math.round(percent*100)+"%)");
			}

      //String sampleInfo = analyze(sample,0); 
      write.close();

			return sample; 
		} 

		/*
		* Sets a given parameter for a given population
		*/
		public void setParam(String pop ,  String param, double rate)
		{
			ArrayList<Population> popset =  new ArrayList<Population>();
			if (pop.equals("all")) popset.addAll(this.pops);
			else 
			{
				for (Population p : this.getPops())
					if (p.getName().equals(pop)) popset.add(p);
			}
			for (Population selected: popset)
			{
				if (param.equals("birth"))
				{
					selected.setbirthrate(rate);
				}
				else if (param.equals("marriage"))
				{
					selected.setmarriagerate(rate);
				}
				else if (param.equals("immigration"))
				{
					selected.setimmigrationrate(rate);
				}
				else if (param.equals("emigration"))
				{
					selected.setemigrationrate(rate);
				}
				else if (param.equals("death"))
				{
					selected.setdeathrate(rate);
				}
			}
		}
	/*
	* reports the by individual statistics for a given population
	*/
		public  void exportstats(ArrayList<Individual> selection, String filename) throws IOException
		{
			PrintWriter w = new PrintWriter(filename, "UTF-8");
			w.println("FirstName,LastName,Gender,Residence,Age,BirthYear,DeathYear,PaternalSiblings,MaternalSiblings,FullSiblings,NumChildren,num_full_siblings,num_half_siblings,RelativesDegree1,RelativesDegree2,RelativesDegree3");
		//	ArrayList<Individual> selection = new ArrayList<Individual>();
		//	selection.addAll(p.getPopulace());
			//selection.addAll(p.getDeparted());
			for (Individual i : selection)
			{
				w.println(i.getName()+","+i.getSurname()+","+i.getGender()+","+i.getResidence().getName()+","+i.getAge()+","+
					  i.getBirthYear()+","+i.getDeathYear()+","+i.getPaternalSiblings().size()+","+i.getMaternalSiblings().size()+","+i.getFullSiblings().size()+","+i.getChildren().size()+","+i.get_num_full_siblings()+","+i.get_num_half_siblings()+","+
					i.getRelatives(1).size()+","+i.getRelatives(2).size()+","+i.getRelatives(3).size());
			}
			w.close();
		}

    /*
    * Exports the by year statistics for a given population
    */
		public void exportannual(String outfile_root) throws IOException
		{
			PrintWriter annum = new PrintWriter(outfile_root + ".region_annalstats.txt" , "UTF-8" );
			annum.print("Population");
			for (int i = 0 ;  i <= (this.year - this.startYear) ; i++) annum.print("\t"+(this.startYear + i));
			annum.println();
			for (Population pop : this.getPops())
			{
				annum.println(pop.getPopString());
				PrintWriter yearstats=new PrintWriter(outfile_root +"."+pop.getName()+"_annualstats.txt","UTF-8");
				yearstats.println("Year\tPopulation\tMarriages\tBirths\tDeaths\tDeaths_to_date\ttotPaternalSiblings\ttotMaternalSiblings\ttotFullSiblings\tAvgChildren\tAvgdeg1Relatives\tAvgdeg2Relatives\tAvgdeg3Relatives\tpartSingle\tpartMarried\tMarriageHitrate");
				yearstats.println(pop.getDataString());
				yearstats.close();
			}
			annum.close();
		}
	}

	/*
	* This method reports statistics on an inclusive collection of individuals
	*/
	public static String analyze( ArrayList<Individual> group)
	{
		ArrayList<Individual> deg1= new ArrayList<Individual>(); 
		ArrayList<Individual> deg2= new ArrayList<Individual>(); 
		ArrayList<Individual> deg3= new ArrayList<Individual>(); 
		double totDeg1=0; 
		double totDeg2=0; 
		double totDeg3=0; 
		double avgDeg1=0; 
		double avgDeg2=0; 
		double avgDeg3=0;
		double numRelated1 = 0 ;  
		double numRelated2 = 0 ;  
		double numRelated3 = 0 ;  
		String degree1networks = getNetworks(group,1); 		
		String degree2networks = getNetworks(group,2); 		
		for (Individual i: group)
		{
      int is_first = 0;
      int is_second = 0;

      for ( Individual rel : i.getRelatives(1))
      {
        if(group.contains(rel))
        {
          numRelated1++;
          numRelated2++;
          numRelated3++;
          is_first = 1;
          break;
        }
      }
      for ( Individual rel : i.getRelatives(2))
      {
        if(is_first == 1)
        {
          break;
        }
        if(group.contains(rel))
        {
          numRelated2++;
          numRelated3++;
          is_second = 1;
          break;
        }
      }
      for ( Individual rel : i.getRelatives(3))
      {
        if(is_first == 1 || is_second == 1)
        {
          break;
        }
        if(group.contains(rel))
        {
          numRelated3++;
          break;
        }
      }
      
      //deg1.clear();
			//deg2.clear();
			//deg3.clear();
			//deg1.addAll(i.getRelatives(1));
			//System.out.println(i + "deg1: "+deg1.size());
			//deg1.retainAll(group);
			//totDeg1 += deg1.size();  
			//deg2.addAll(i.getRelatives(2));
			//System.out.println(i + "deg2: "+deg2.size());
			//deg2.retainAll(group); 
			//totDeg2 += deg2.size();  
			//deg3.addAll(i.getRelatives(3));
			//System.out.println(i + "deg3: "+deg3.size());
			//deg3.retainAll(group); 
			//totDeg3 += deg3.size(); 
			//if ( deg1.size() != 0) numRelated1 ++; 
			//if ( deg2.size() != 0) numRelated2 ++; 
			//if ( deg3.size() != 0) numRelated3 ++; 
		}
		numRelated1 = numRelated1 / (double)group.size();
		numRelated2 = numRelated2 / (double)group.size();
		numRelated3 = numRelated3 / (double)group.size();
		avgDeg1 = totDeg1/(double)group.size();
		avgDeg2 = totDeg2/(double)group.size();
		avgDeg3 = totDeg3/(double)group.size();
	//	System.out.println("Average # of 1st degree relatives within sample: "+ avgDeg1); 	
	//	System.out.println("Average # of 2nd degree relatives within sample: "+ avgDeg2); 	
	//	System.out.println("Average # of 3rd degree relatives within sample: "+ avgDeg3); 	
	//	System.out.println("% related by 1 degree: " + numRelated1);
	//	System.out.println("% related by 2 degrees: " + numRelated2);
	//	System.out.println("% related to 3 degrees: " + numRelated3);
		return(group.size()+","+numRelated1+","+numRelated2+","+numRelated3+","+totDeg1+","+totDeg2+","+totDeg3);
	}

	/*
	* Collects all of the individuals in a group that form a direct network of the given degree to a chosen individual 
	*/	
	public static HashSet<Individual> gatherNetwork(Individual chosen, HashSet<Individual> visited, ArrayList<Individual> group,int degree)
	{
		visited.add(chosen);
		ArrayList<Individual> rel = new ArrayList<Individual>();
		rel.addAll(chosen.getRelatives(degree));
		rel.retainAll(group); // remove all relatives not in the sample
		rel.removeAll(visited); // remove all relatives that have already been visited 
		//visited.addAll(rel);  
		for (Individual i : rel)
		{
      if(! visited.contains(i))
      {
			  gatherNetwork(i, visited, group, degree); 
      }
		}
		return visited; 
	}
	/*
	* Retrives all of the familial networks from a sample
	*/
	public static String getNetworks(ArrayList<Individual> group, int degree) 
	{
		ArrayList<HashSet<Individual>> networks = new ArrayList<HashSet<Individual>>();
		ArrayList<Individual> pool = new ArrayList<Individual>();
		ArrayList<Individual> largest = new ArrayList<Individual>();
		ArrayList<Individual> current_list = new ArrayList<Individual>();
		pool.addAll(group);
		Individual chosen;
		double totNetwork = 0;
		double numNetworks = 0; 
		double avgSize = 0 ; 
		try
		{
			PrintWriter write  = new PrintWriter("all_networks_sample"+group.size()+"deg"+degree+".txt", "UTF-8");
			PrintWriter edges  = new PrintWriter("largestNetwork"+group.size()+"deg"+degree+".dot", "UTF-8");
			PrintWriter all_edges  = new PrintWriter("close_relationships"+group.size()+"deg"+degree+".txt", "UTF-8");
			write.println("NetSize");
			HashSet<Individual>  current = new HashSet<Individual>();
			
      // For each individual
      while (!pool.isEmpty())
			{
				numNetworks++;
				current.clear();
				chosen = pool.get(0);
        //System.out.println("HERE0");
				current = gatherNetwork(chosen, new HashSet<Individual>(), group, degree); // Get the current network
        //System.out.println("HERE");
				pool.removeAll(current);
				totNetwork += current.size() ;
				write.println(current.size()+ " "+ current);
				networks.add(current); 
				if (current.size() > largest.size() )
				{
				 	largest.clear();
					largest.addAll(current);
				}
			  
        // For each samples in the current network
        current_list.addAll(current);
        while (!current_list.isEmpty())
        {
          Individual i = current_list.get(0);
          for (Individual rel1: i.getRelatives(1))
          {
            if(current_list.contains(rel1))
            {
              if(i.getFullSiblings().contains(rel1))
              {
                all_edges.println(i+" "+rel1 +" FS 1st");
              }
              else
              {
                all_edges.println(i+" "+rel1 +" PO 1st");
              }
            }
          }
          for (Individual rel2: i.getSecondDegreeRelatives())
          {
            if(current_list.contains(rel2))
            {
              all_edges.println(i+" "+rel2 +" HAG 2nd");
            }
          }
          // I could implement output all 3rd degree relationships

          // Remove the sample from the list
          current_list.remove(i);
        }	

			}
			
			avgSize = totNetwork/numNetworks; 
			System.out.println(degree+" degree networks : "+ numNetworks); 
			System.out.println("average network size: "+ avgSize); 
			System.out.println("largest network size: " + largest.size());
			write.close();
			all_edges.close();
			edges.println("graph network2 {");
			edges.println("node[shape=circle];");
			while (!largest.isEmpty())
			{
				Individual i = largest.get(0);
				for (Individual rel1: i.getRelatives(1))
				{
					if(largest.contains(rel1)) edges.println("\""+i+"\"--\""+rel1 +"\" [color=red];");
				}
				for (Individual rel2: i.getSecondDegreeRelatives())
				{
					if(largest.contains(rel2)) edges.println("\""+i+"\"--\""+rel2+"\" [color=blue];" );
				}
				largest.remove(i); 
			}	
			edges.println("}");
			edges.close();
		}
		catch(IOException e)
		{
			//oops
		}
		return ","+numNetworks+","+avgSize; 
	}

	/*
	* Defines the correct command line input to run SimProgeny. Exits the program upon invalid input. 
	*/
	public static void usage()
	{
		System.out.println("Usage: java SimProdigy <population_file> [configuration_file]");
		System.exit(1); 
	}

	/*
	* Defines the correct command line input to run SimProgeny. Exits the program upon invalid input. 
	*/
	public static void input_configuration_file(String config_file)
	{
    System.out.println("Reading configuration file: " + config_file);
    StringBuilder error_messages = new StringBuilder();
    int error = 0;
    try
    {
    try
    {
    }
    catch(NumberFormatException e)
    {
      System.err.println("Duration must be a positive integer");
    }
      
      int rerun = 1;
      int iteration = 0;
      while (rerun == 1 && iteration < 2)
      {
        error_messages.delete(0,error_messages.length());
        BufferedReader br = new BufferedReader(new FileReader(config_file)); 	
        String line; 
        rerun = 0;
        error = 0;
        iteration++;
        while ((line = br.readLine()) != null)
        {
          if(line == null || line.isEmpty() || line.matches("^#"))
          {
            continue;
          }
          String key = line.split(" ")[0];
          String value = line.split(" ")[1];

          //System.out.println(key + " " + value);
          
          if (key.equals("birthrate"))
          {
            try
            {
              config.birthrate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("Birthrate must be a value > 0");	
              error = 1;
            }
          }
          else if (key.equals("deathrate"))
          {
            try
            {
              config.deathrate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("Birthrate must be a value > 0");	
              error = 1;
            }
          }
          else if (key.equals("marriagerate"))
          {
            try
            {
              config.marriagerate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("marriage rate must be a value > 0");	
              error = 1;
            }
          }
          else if (key.equals("immigrationrate"))
          {
            try
            {
              config.immigrationrate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("immigrationrate must be a value between 0 and 1");	
              error = 1;
            }
          }
          else if (key.equals("emigrationrate"))
          {
            try
            {
              config.emigrationrate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("emigrationrate must be a value between 0 and 1");	
              error = 1;
            }
          }
          else if (key.equals("divorcerate"))
          {
            try
            {
              config.divorcerate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("divorcerate must be a value > 0");	
              error = 1;
            }
          }
          else if (key.equals("full_sibling_rate"))
          {
            try
            {
              config.full_sibling_rate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("full_sibling_rate must be a value between 0 and 1");	
              error = 1;
            }
          }
          else if (key.equals("internal_migration_rate"))
          {
            try
            {
              config.internal_migration_rate = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("internal_migration_rate must be a value between 0 and 1");	
              error = 1;
            }
          }
          else if (key.equals("fertilityStart"))
          {
            try
            {
              config.fertilityStart = Integer.parseInt(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("fertilityStart must be a value > 0");	
              error = 1;
            }
          }
          else if (key.equals("fertilityEnd"))
          {
            try
            {
              config.fertilityEnd = Integer.parseInt(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("fertilityEnd must be a value > fertilityStart and < 120");	
              error = 1;
            }
            rerun++;
          }
          else if (key.equals("male_mortality_by_age"))
          {
            config.male_mortality_by_age = new ArrayList<Double>();
            for (String s : value.split(","))
            {
              try
              {
                if(Double.parseDouble(s) > 1 || Double.parseDouble(s) < 0)
                {
                 error_messages.append("INPUT ERROR: all age based rates must be between 0 and 1.\n");
                  error = 1;
                }
                config.male_mortality_by_age.add(Double.parseDouble(s));
              }
              catch(NumberFormatException e)
              {
                System.err.println("male_mortality_by_age values must between 0 and 1");	
                error = 1;
              }
            }
            if(config.male_mortality_by_age.size() != 121)
            {
              error_messages.append("INPUT ERROR: male_mortality_by_age must have 121 comma separated values between 0 and 1, representing ages 0 - 120.\n");
              error = 1;
            }
          }
          else if (key.equals("female_mortality_by_age"))
          {
            config.female_mortality_by_age = new ArrayList<Double>();
            for (String s : value.split(","))
            {
              try
              {
                if(Double.parseDouble(s) > 1 || Double.parseDouble(s) < 0)
                {
                  error_messages.append("INPUT ERROR: all age based rates must be between 0 and 1.\n");
                  error = 1;
                }
                config.female_mortality_by_age.add(Double.parseDouble(s));
              }
              catch(NumberFormatException e)
              {
                System.err.println("female_mortality_by_age values must between 0 and 1");	
                error = 1;
              }
            }
            if(config.female_mortality_by_age.size() != 121)
            {
              error_messages.append("INPUT ERROR: female_mortality_by_age must have 121 comma separated values between 0 and 1, representing ages 0 - 120.\n");
              error = 1;
            }
          }
          else if (key.equals("fertility_by_age"))
          {
            config.fertility_by_age = new ArrayList<Double>();
            for (String s : value.split(","))
            {
              try
              {
                if(Double.parseDouble(s) > 1 || Double.parseDouble(s) < 0)
                {
                  error_messages.append("INPUT ERROR: all age based rates must be between 0 and 1.\n");
                  error = 1;
                }
                config.fertility_by_age.add(Double.parseDouble(s));
              }
              catch(NumberFormatException e)
              {
                System.err.println("fertility_by_age values must between 0 and 1");	
                error = 1;
              }
            }
            if(config.fertility_by_age.size() < config.fertilityEnd)
            {
              error_messages.append("INPUT ERROR: fertility_by_age must have comma separated values (between 0 and 1) for ages 0 through the max age of fertility (fertilityEnd = 49 by default).\n");
              error = 1;
            }
          }
          else if (key.equals("male_marriage_by_age"))
          {
            config.male_marriage_by_age = new ArrayList<Double>();
            for (String s : value.split(","))
            {
              try
              {
                if(Double.parseDouble(s) > 1 || Double.parseDouble(s) < 0)
                {
                  error_messages.append("INPUT ERROR: all age based rates must be between 0 and 1.\n");
                  error = 1;
                }
                config.male_marriage_by_age.add(Double.parseDouble(s));
              }
              catch(NumberFormatException e)
              {
                System.err.println("male_marriage_by_age values must between 0 and 1");	
              error = 1;
              }
            }
            if(config.male_marriage_by_age.size() < config.fertilityEnd)
            {
              System.out.println("INPUT ERROR: male_marriage_by_age must have comma separated values (between 0 and 1) for ages 0 through the max age of fertility (fertilityEnd = 49 by default).\n");
              error = 1;
            }
          }
          else if (key.equals("female_marriage_by_age"))
          {
            config.female_marriage_by_age = new ArrayList<Double>();
            for (String s : value.split(","))
            {
              try
              {
                if(Double.parseDouble(s) > 1 || Double.parseDouble(s) < 0)
                {
                  error_messages.append("INPUT ERROR: all age based rates must be between 0 and 1.\n");
                  error = 1;
                }
                config.female_marriage_by_age.add(Double.parseDouble(s));
              }
              catch(NumberFormatException e)
              {
                System.err.println("female_marriage_by_age values must between 0 and 1");	
              error = 1;
              }
            }
            if(config.female_marriage_by_age.size() < config.fertilityEnd)
            {
              System.out.println("INPUT ERROR: female_marriage_by_age must have comma separated values (between 0 and 1) for ages 0 through the max age of fertility (fertilityEnd = 49 by default).\n");
              error = 1;
            }
          }
          else if (key.equals("ascertainment_approach"))
          {
            value.toLowerCase();
            if(!value.toLowerCase().equals("random") && !value.toLowerCase().equals("clustered"))
            {
              error_messages.append("INPUT ERROR: ascertainment_approach must be either \"random\" or \"clustered\"\n");
              error = 1;
            }
            config.ascertainment_approach = value.toLowerCase();
          }
          else if (key.equals("ordered_sampling_proportion"))
          {
            try
            {
              config.ordered_sampling_proportion = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("proportion_initially_married must be a value between 0 and 1");
              error = 1;
            }
          }
          else if (key.equals("lambda1stDegreeClusteredAscertainment"))
          {
            try
            {
              config.lambda1stDegreeClusteredAscertainment = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              System.err.println("lambda1stDegreeClusteredAscertainment must be a value > 0");	
              error = 1;
            }
          }
          else if (key.equals("lambda2ndDegreeClusteredAscertainment"))
          {
            try
            {
              config.lambda2ndDegreeClusteredAscertainment = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("lambda2ndDegreeClusteredAscertainment must be a value > 0");
              error = 1;
            }
          }
          else if (key.equals("proportion_initially_married"))
          {
            try
            {
              config.proportion_initially_married = Double.parseDouble(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("proportion_initially_married must be a value between 0 and 1");
              error = 1;
            }
          }
          else if (key.equals("burn_in_period"))
          {
            try
            {
              config.burn_in_period = Integer.parseInt(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("INPUT ERROR: burn_in_period must be a value from 0 to 500, representing the number of years to burn-in. Default is 100.\n");
              error = 1;
            }
            if(config.burn_in_period < 0 || config.burn_in_period > 500)
            {
              error_messages.append("INPUT ERROR: burn_in_period must be a value from 0 to 500, representing the number of years to burn-in. Default is 100.\n");
              error = 1;
            }
          }
          else if (key.equals("simulation_period"))
          {
            try
            {
              config.simulation_period = Integer.parseInt(value);
            }
            catch(NumberFormatException e)
            {
              error_messages.append("simulation_period must be an integer between 0 and 10000");	
              error = 1;
            }
            if(config.simulation_period < 0 || config.simulation_period > 10000)
            {
              error_messages.append("INPUT ERROR: simulation_period must be a value from 0 to 10000, representing the number of years to simulate. Default is 200.\n");
              error = 1;
            }
          }
          else
          {
            error_messages.append("No parameter match for " + key + "\n");
          }
        }
        br.close();
      }
		}
		catch (java.io.IOException e)
		{
      System.out.println("IOException error: " + e);
      System.exit(1);
		}

    if(error == 1)
    {
      System.err.println("\nExiting due to the following input configuration error(s):\n" + error_messages);
      System.exit(1);
    }
	}

	/*
	* Exports a ped file for the individual from the given population at the index. Relatives of 
	* the given degree are included in the pedigree.  
	*/
	public static void exportSinglePed(Population p, String filename, int index, int degree) throws IOException
	{
		String gender = "3";
		String father = "0";
		String mother = "0";
	//	w.println("FamilyID\tIndividualID\tPaternalID\tMaternalID\tSex\tPhenotype\tAge");
		Individual chosen =  p.getPopulace().get(index);
		PrintWriter w = new PrintWriter(chosen.toString()+"_degree"+degree+".ped", "UTF-8");
		HashSet<Individual> selection = new HashSet<Individual>();
		HashSet<Individual> parents = new HashSet<Individual>();
    selection.add(chosen);
		for (int d = 1; d <= degree; d++)
			selection.addAll(chosen.getRelatives(d));
		for (Individual c : selection )
		{
			parents.add(c.getMother());
			parents.add(c.getFather());
		}
		parents.remove(null);
		selection.addAll(parents);
		for (Individual i : selection)
		{
			gender = "3";
			father = "0";
			mother = "0";
			if (selection.contains(i.getMother())) mother = i.getMother().toString();
			if (selection.contains(i.getFather())) father = i.getFather().toString();
			if (i.getGender()==0)gender = "2"; else gender = "1";
			w.println("fam\t"+i+"\t"+father+"\t"+mother+"\t"+gender+"\t"+"0\t"+i.getAge());
		}
		w.close();
	}	
	/*
	* The main function will execute the simulation and present the results. 
	* After the simulation is complete, the main method will launch a command line user interface where the 
	* user can define what data is to be extracted from the simulated poulation in what format.  
	*/
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1 || args.length > 2) usage();

		// Set population parameters
		ArrayList<PrintWriter> yearTrackers= new ArrayList<PrintWriter>();	

		// Read in command line arguments
		String pops_file = args[0];

    // Set parameters to default
		String line;
		String[] tokens;
		String first = "";
		String second = "";
		String third = "";
		String fourth = "";
		String fifth = "";
		String sixth = "";
		int period = 1;
		int index = 0;
		String name = ""; 
    int ascertainment_order = -1;
    int n = 1000;
		String config_file = "default";
    
    
    // Read in configuations if provided
    if (args.length == 2)
    {
		  config_file = args[1];
      input_configuration_file(config_file);
    }
    
    
		// Read in and store first and last names for individual identification
		Scanner scan = new Scanner(System.in);
		Random r =  new Random();
		BufferedReader br1 = new BufferedReader(new FileReader("../resources/female_names.txt")); 	
		ArrayList<String> female_names = new ArrayList<String>();
		for(String line1; (line1 = br1.readLine()) != null; ) 
		{
			 female_names.add(line1.split(" ")[0].toLowerCase());
	  }
		br1.close();
		BufferedReader br2 = new BufferedReader(new FileReader("../resources/male_names.txt")); 	
		ArrayList<String> male_names = new ArrayList<String>();
		for(String line2; (line2 = br2.readLine()) != null; ) 
		{
			male_names.add(line2.split(" ")[0].toLowerCase());
		}
		br2.close();
		BufferedReader br3 = new BufferedReader(new FileReader("../resources/last_names.txt"));
		ArrayList<String> last_names = new ArrayList<String>();
		for(String line3; (line3 = br3.readLine()) != null; )
		{
			last_names.add(line3.split(" ")[0].toLowerCase());
		}
		br3.close();

		// read in and store list of city names used for population identification.
		BufferedReader br4 = new BufferedReader(new FileReader("../resources/cities.txt"));
		ArrayList<String> city_names = new ArrayList<String>();
		for(String line4; (line4 = br4.readLine()) != null; )
		{
			city_names.add(line4.replaceAll(" ", "_").toLowerCase());
		}
		br4.close();
		// Create new region object 
		Region reg = new Region(city_names);
		

    // Read in pops.txt file to create populations 
		try
		{
			BufferedReader br5;
			br5  = new BufferedReader(new FileReader(pops_file));
			br5.readLine(); // skip first line
			for(String line5; (line5 = br5.readLine()) != null; )
			{
				String[] poptokens = line5.split("\\s+");
        //System.out.println("popstokens length: " + poptokens.length);
				if (poptokens.length < 2 || poptokens.length > 3)
				{
					System.err.println("\nPopulation File ERROR: must have \"populations\" and \"size\" as the first two columns with an option third column specifying the \"ascertainment_order\"\n");
					System.exit(1);
				}
				try
				{
					// define a new population for each line in the file
					name = poptokens[0];
					n = Integer.parseInt(poptokens[1]);
          ascertainment_order = -1;
          if (poptokens.length == 3)
				  {
            ascertainment_order = Integer.parseInt(poptokens[2]);
          }
				}
				catch (NumberFormatException e)
				{
					System.err.println("Error: start size must be a positive integer, birthrate and marriage rate must be doubles");
					break; 
				} 
				System.out.println("\nInitializing " + name + " order " + ascertainment_order);
				reg.addPop( new Population(
					reg,
					name,
					n,
          ascertainment_order,
					config.birthrate,
					config.deathrate,
					config.marriagerate,
					config.immigrationrate,
					config.emigrationrate,
          config.full_sibling_rate,
          config.internal_migration_rate,
          config.divorcerate,
          config.fertilityStart,
          config.fertilityEnd,
          config.male_mortality_by_age,
          config.female_mortality_by_age,
          config.male_marriage_by_age,
          config.female_marriage_by_age,
          config.fertility_by_age,
					female_names,
					male_names,
					last_names)); 
				System.out.println(name+" initialized\n");
			}
			br5.close();

		}
		catch (FileNotFoundException e)
		{
      System.err.println("Could not find populations input file: " + e);
		}

    // Print population size
    System.out.println("Initial Population: "+reg.getSize());
    
    String ped_file_name = "Initial_fam_file.fam";
    reg.exportPed(ped_file_name);

    // Run burn-in phase
    reg.burnIn(config.burn_in_period);

    //ped_file_name = "Burn-in_fam_file.fam";
    //reg.exportPed(ped_file_name);

    //for (Population p : reg.getPops())
    //{
		//  exportSinglePed(p,p.getName()+ "_pedigree.txt", 1, 3);
    //}

    // Run simulation
    if (config.simulation_period > 0)
    {
      for (int y = 0; y < config.simulation_period; y++)
      {
         reg.elapseRegionYear(false, 10);
      }
      reg.fix_duplicate_names();
    }
    String filename= pops_file + "." + config_file + ".popStats.txt";
    reg.exportstats(reg.getPopulace(),filename);
    reg.exportannual(pops_file + "." + config_file);
    ped_file_name = pops_file + "." + config_file + ".final_fam_file.fam";
    reg.exportPed(ped_file_name);

    // Ascertain samples
    ArrayList<Individual> sample = reg.ascertain(pops_file + "." + config_file);

    // Analyze samples
    String cmd = "./analyze_simulation_results.pl " + ped_file_name + " " + pops_file + "." + config_file + ".ascertainment_order";
    System.out.println("cmd: " + cmd);
    
    Runtime rt = Runtime.getRuntime();
   
    try 
    {
      Process p = rt.exec(cmd);
      InputStream in = p.getInputStream();
      BufferedInputStream buf = new BufferedInputStream(in);
      InputStreamReader inread = new InputStreamReader(buf);
      BufferedReader bufferedreader = new BufferedReader(inread);

      // Read the ls output
      line = "";
      while ((line = bufferedreader.readLine()) != null) 
      {
          System.out.println(line);
      }
      // Check for ls failure
      try 
      {
         if (p.waitFor() != 0) {
              System.err.println("exit value = " + p.exitValue());
          }
      } 
      catch (InterruptedException e) 
      {
          System.err.println(e);
      } 
      finally 
      {
          // Close the InputStream
          bufferedreader.close();
          inread.close();
          buf.close();
          in.close();
      }
    } 
    catch (IOException e) 
    {
      System.err.println(e.getMessage());
    } 
      
    //String sampleInfo = analyze(sample);
    //exportstats(sample , "randomSample"+sampleSize+".txt");

    return;
  }
/*
    //// DISABLING USER INTERFACE 
		// Launch commad line interface	
		while (true)
		{
			ArrayList<Population> pops = reg.getPops();
			if (tracking) System.out.print("(tracking)");
			System.out.print("-->");
			line = "";
			first = "";
			tokens = null;
			line = scan.nextLine();
			tokens = line.split(" ");
			if (line.length() > 0) first = tokens[0];
			if(tokens.length > 1) second = tokens[1];
			if (tokens.length >2) third = tokens[2];
			if (tokens.length >3) fourth = tokens[3];
			if (tokens.length >4) fifth = tokens[4];
			if (tokens.length >5) sixth = tokens[5];
			if (first == "") // if no input is given 
			{
				continue;
			}
			else if (first.equals("quit")) // leave interface and return to console
			{
				System.exit(2);
			}
			else if (first.equals("help")) // get list of valid commands
			{
				System.out.println("Valid commands: \nquit - leave interface");
				System.out.println("stats - export information about all indivduals in a population to a text file");
				System.out.println("burn - specify the number of years to progress without changing population size"); 
				System.out.println("ped - export ped file for a select individual from a population"); 
				System.out.println("\tOptional second agument to set the outermost degree of relatives to be included.");
				System.out.println("elapse - elapse a population one year unless a greater number of years is specified");
				System.out.println("\toptional second argument to define the number of years to elapse");
				System.out.println("\toptional third argument to define the frequency that annual data will be gathered");
				System.out.println("\toptional third or fourth argument __static__ to maintain a static population size for the given duration");
				System.out.println("add - add a new population to the region.\n\toptional second argument to select population name");
				System.out.println("\toptional third argument to define birthrate");
				System.out.println("\toptional fourth argument to define marriagerate");
				System.out.println("sample - take a random sample from all populations");
				System.out.println("\toptional second argument to specify a sample size");
			}
			else if (first.equals("stats")) // export stats by individual
			{
				String filename= "popStats.txt";
				if (tokens.length > 1) filename = second+ "_"+filename; 
				reg.exportstats(reg.getPopulace(),filename);
			}

			// take a random sample across all regions
			else if (first.equals("sample"))
			{
				int sampleSize = 1000; 
				try 
				{
					if (tokens.length > 1){sampleSize =Integer.parseInt(second);  }
				}
				catch (NumberFormatException e) 
				{
					System.err.println("Error: Sample size must be a positive integer"); 
					continue;
				}
				ArrayList<Individual> sample  =  reg.sample(sampleSize);
				String sampleInfo = analyze(sample,0); 
				if (tracking) write.println("random,"+sampleInfo); 
				//exportstats(sample , "randomSample"+sampleSize+".txt");
			}
			// take a clustered sample across all regions
			else if (first.equals("cluster"))
			{
				int sampleSize = 1000; 
				try 
				{
					if (tokens.length > 1) sampleSize = Integer.parseInt(second); 
					if(tokens.length  > 2)
					{
						lambda1stDegreeClusteredAscertainment = Double.parseDouble(third);
						lambda2ndDegreeClusteredAscertainment = Double.parseDouble(fourth);
					}
			}
				catch (NumberFormatException e) 
				{
					System.err.println("Error: Sample size must be a positive integer"); 
				}
				ArrayList<Individual> sample  =  reg.clusteredSample(sampleSize, lambda1stDegreeClusteredAscertainment, lambda2ndDegreeClusteredAscertainment);
				String sampleInfo = analyze(sample,0); 
				if (tracking) write.println("cluster,"+sampleInfo); 
				//exportstats(sample, "clusteredsample"+sampleSize+".txt");
				
			}
			// take a clustered sample across all regions
			else if (first.equals("order"))
			{
				int sampleSize = 1000; 
				int num_to_sample_from_a_population  = 500;
				boolean cluster = false; 
        int num_populations_to_order_sample_before_random = 1;

				try 
				{
					if (tokens.length >= 2 ) sampleSize = Integer.parseInt(second); 
					if(tokens.length  >= 3)
          {
						if (third.equals("cluster")) cluster = true;  
				  }
					if (tokens.length >= 4 )
				  {
						num_to_sample_from_a_population = Integer.parseInt(fourth);
						System.out.println("Changed num_to_sample_from_a_population: " + num_to_sample_from_a_population); 	
				  }
					if (tokens.length >= 5 )
				  {
						lambda1stDegreeClusteredAscertainment = Double.parseDouble(fifth);
						lambda2ndDegreeClusteredAscertainment = Double.parseDouble(sixth);
				  }
			  }
				catch (NumberFormatException e) 
				{
					System.err.println("Error: Sample size must be a positive integer");
					continue; 
				}
				ArrayList<Individual> sample = reg.orderedSample(sampleSize,  cluster,  num_to_sample_from_a_population, num_populations_to_order_sample_before_random, lambda1stDegreeClusteredAscertainment, lambda2ndDegreeClusteredAscertainment);
				String sampleInfo = analyze(sample, num_to_sample_from_a_population); 
				if (tracking) write.println("order,"+sampleInfo); 
				//exportstats(sample, "clusteredsample"+sampleSize+".txt");
				
			}
			else if (first.equals("ped")) // export ped file
			{
				for (Population p : pops)
				{
					index = Math.abs(r.nextInt()%p.getSize());
					try
					{
						if (tokens.length == 1) exportped(p,p.getName()+ "_pedigree.txt", index, 2);
						else if (tokens.length == 2) exportped(p,p.getName()+ "_pedigree.txt",index, Integer.parseInt(second));
					}
					catch (NumberFormatException e) 
					{
						System.err.println("Error: Degree must be a positive integer"); 
					}
				}
			}
			else if (first.equals("temp")) // export txt file of by year data for each population
			{
				reg.exportannual();
			}
			else if (first.equals("elapse")) // elapse a certain number of years
			{
				long start = System.nanoTime();
				boolean keepStatic = false; 
				int survey = 30;
				if (tokens.length > 2  && third.equals("static")) keepStatic = true;
				if (tokens.length > 3  && fourth.equals("static")) keepStatic = true;
				if (tokens.length==1) reg.elapseYear(keepStatic, survey);
				else	
				{
					try 
					{
						if (tokens.length ==3 &&!keepStatic )  
						{
							survey = Integer.parseInt(third); 
					//		System.out.println("Setting survey period: " + survey);
						}
						period = Integer.parseInt(second);
						if (period > 0)
						{
							for (int y = 0; y< period;y++)
								reg.elapseYear(keepStatic, survey);
						}
						else
							System.err.println("Error: period and survey frequency must be greater than 0"); 
					}
					catch (NumberFormatException e)
					{
						System.err.println("Error: Period must be an integer"); 
					}
				}
				long finish = System.nanoTime();
				System.out.println(period +" years elapsed in "+ (finish-start)/1000000000 + " seconds");
			}
			else if (first.equals("info")) // get information on currently running populations
			{
				for (Population p : pops)
					System.out.println(p);
			}
			else if (first.equals("add")) // add new population
			{
        name = ""; // Set to empty, otherwise it uses the name of the last population added
				try
				{
					if (tokens.length == 2)
					{
						name = second;  
					}
					else if (tokens.length == 3) 
					{
						name = second; 
						birthrate = Double.parseDouble(third); 
					}
					else if (tokens.length == 4) 
					{
						name = second; 
						birthrate = Double.parseDouble(third); 
						marriagerate = Double.parseDouble(fourth); 
					}			
				}
				catch(NumberFormatException e)
				{
					System.err.println("Error: birthrate and marriage rate must both be doubles.");
					continue; 
				}
				reg.addPop( new Population(
					reg,
					name,
					n,  
					reg.getYear(),
					birthrate, 
					deathrate,
					marriagerate,
					immigrationrate, 
					emigrationrate, 
					full_sibling_rate,
          internal_migration_rate,
          divorcerate,
          fertilityStart,
          fertilityEnd,
          male_mortality_by_age,
          female_mortality_by_age,
					female_names, 
					male_names, 
					last_names)); 
			}
			else if (first.equals("sys")) // run a system command from within simprogeny
			{
				String s = null;
				String command = scan.nextLine(); 
				try 
				{
					Process p = Runtime.getRuntime().exec(command);
					BufferedReader stdIn = new BufferedReader(new 
						InputStreamReader(p.getInputStream()));
					BufferedReader stdErr = new BufferedReader(new 
						InputStreamReader(p.getErrorStream()));
					while ((s = stdIn.readLine()) != null)
					{
						System.out.println(s);
					}
					while ((s = stdErr.readLine()) != null)
					{
						System.out.println(s);
					}
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			else if (first.equals("track")) // start tracking samples
			{
				if (tracking) continue; 	
				write = new PrintWriter("samples.txt", "UTF-8");
				write.println("Method,SampleSize,Deg1,Deg2,Deg3,totDeg1,totDeg2,totDeg3");
				tracking = true;  
			}
			else if (first.equals("stop")) // stop tracking samples
			{
				write.close();
			}
			// elapse a given number of years without population growth or decline 
			else if (first.equals("burn")) 
			{
				int duration =1; 
				try
				{
					if (tokens.length >1 )duration = Integer.parseInt(second); 
					reg.burnIn(duration);
				}
				catch(NumberFormatException e)
				{
					System.err.println("Duration must be a positive integer");	
					continue;
				}
			}
			// adjust a parameter
			else if (first.equals("set"))
			{
				String popname = "all";
				String param= "all";
				double rate = 0.00; 
				if (tokens.length != 4) 
				{	
					System.err.println("Error, set command requires three arguments");
					continue; 
				}
				popname = second;
				param = third; 
				try 
				{
					rate = Double.parseDouble(fourth);
				}
				catch(NumberFormatException e)
				{
					System.err.println("Rate must be a double");
					continue; 
				}
				reg.setParam(popname, param, rate);

			}
			else if (first.equals("clear"))
			{
				reg.clear();
			}
			else System.out.println("command not found"); // if a non valid command is entered
		}
	}
  */
}

