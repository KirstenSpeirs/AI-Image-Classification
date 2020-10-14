package dissertation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.utils.FileUtils;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.imagefilter.AbstractImageFilter;

public class ImageProcess {
String location;
String imgLoc;
double bestCar;
int bestCount = 0;
double newCar = 0;
int count;
DecimalFormat df2 = new DecimalFormat("#.###############");
double[] values; 
double max = Double.NEGATIVE_INFINITY;//must be lower than possible value of instance categorisation score
double second = Double.NEGATIVE_INFINITY;//a placeholder to compare each value

ImageProcess(String l){
	this.location = l;
}

public void setCount(int c) {
	this.count = c;
}

public double getCount() {
	return count;
}

public void setBestCar(double b) {
	this.bestCar = b;
}

public Double getBestCar() {
	return bestCar;
}

public void setBestCount(int b) {
	this.bestCount = b;
}

public int getBestCount() {
	return bestCount;
}

public void setNewCar(double car) {
	this.newCar = car;
}

public Double getNewCar() {
	return newCar;
}

public void setImgLoc(String imgloc) {
	this.imgLoc = imgloc;
}


public void resetArff() {
	Path path = Paths.get("plane.arff");
	Charset charset = StandardCharsets.UTF_8;
	String content;
	
	try {
		String line = Files.readAllLines(Paths.get("plane.arff")).get(4);
		System.out.println("line"+line);
		content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll(line, "plane0.jpg,PLANE");
		Files.write(path, content.getBytes(charset));
		
	} catch (IOException e) {
		System.out.println("Error replacing image name: " + e);
		e.printStackTrace();
	}
	
}

	public Instances createModel() {
		//choose classifier(s)
		try {
			
			//get data
			DataSource source = new DataSource(location + "/removedPlaneVScarFiltered.arff");
			Instances data = source.getDataSet();
			data.setClassIndex(data.numAttributes()-1);
			data.randomize(new Random());
			
			//split data for training and testing
			Instances training = data.trainCV(2, 0);
			Instances testing = data.testCV(2, 0);			
			
			//build classifier with training data
			NaiveBayes nb = new NaiveBayes();
			nb.buildClassifier(training);
			weka.core.SerializationHelper.write("nb", nb);//store model
			
			//evaluate the model with test data
			Evaluation eval = new Evaluation(training);
			eval.evaluateModel(nb, training);
			System.out.println(eval.toSummaryString("\nResults\n",false));
			System.out.println("CORRECT ON TRAINING DATA: " + eval.pctCorrect()/100);
			eval.evaluateModel(nb, testing);
			System.out.println(eval.toSummaryString("\nResults\n",false));
			System.out.println("CORRECT ON TEST DATA: " + eval.pctCorrect()/100);
			
			results(data, nb);	
			
			return data;
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean loadModel() throws Exception {
		
			Classifier cls = (Classifier) weka.core.SerializationHelper.read(location + "/nb");		
			DataSource source;
			
			//bestCount = count;
			source = new DataSource("removed/removedPlaneFiltered"+count+".arff");
			
			Instances data = source.getDataSet();
			data.setClassIndex(data.numAttributes()-1);
			//data.randomize(new Random());
			
			Evaluation eval = new Evaluation(data);
			eval.evaluateModel(cls, data);
			//System.out.println(eval.toSummaryString("\nResults\n",false));
			
			boolean result = results(data, cls);
			
			
//			if(result>=1) {
//				return true;
//			}
			
		
		
		return result;
	}
	
	public void alterImage() {
		File file;
		//System.out.println(count);
		
		if(count==1) {
			file = new File(location + "/image");
		}else {
			file = new File(location + "/newImage");
		}
		
		//get images
		BufferedImage image = getImage(file);
		
		
		//change image
		image = changePixel(image);
			
		//save new image
		saveImage(image);
		
		//System.out.println("Image Altered");
			
		
	}
	
	/*saving images in folder*/
	private void saveImage(BufferedImage img) {
		try {
		    File outputfile = new File("newImage/plane"+count+".jpg");
		    System.out.println("file save location: " + outputfile);
		    ImageIO.write(img, "jpg", outputfile);
		} catch (IOException e) {
			System.out.println("Error saving Image: " + e);
		}
	}

	/*load in images*/
	private BufferedImage getImage(File file) {
		BufferedImage img = null;
		if(count==1) {
			try {
				//change this to fit the original image's file location
				img = ImageIO.read(new File(imgLoc));			    
			} catch (IOException e) {
				System.out.println("Error loading Image: " + e);
			}
		}else {
			try {
//				img = ImageIO.read(new File("newImage/plane"+bestCount+".jpg"));
//				System.out.println("Using Image: newImage/plane"+bestCount+".jpg");
				img = ImageIO.read(new File("newImage/plane"+(count-1)+".jpg"));
				System.out.println("Using Image: newImage/plane"+(count-1)+".jpg");
			} catch (IOException e) {
				System.out.println("Error loading Image: " + e);
			}
		}
		
		return img;
	}
	
	private static BufferedImage changePixel(BufferedImage img) {
			if(img!=null) {
				int width = img.getWidth();
			    int height = img.getHeight();
			    
			    Random r = new Random();
			    
			    //random x co-ord
			    int xx = r.nextInt(width);
			    
			    //random y co-ord
			    int yy = r.nextInt(height);
		
			    //colour pixel is changed to
			    Color colour = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
			    int pixel = colour.getRGB();
			    
			    //set pixel colour
			    img.setRGB(xx, yy, pixel);
			    
			    System.out.println("co-ord : (" + xx + "," + yy +") \nColour: " + colour);
			    
			}
		
		return img;
	}
	
	private boolean results(Instances data, Classifier cls) throws Exception {
		// calc accuracy
//		int correct = 0;
//		for (Instance i : data) {
//			double prediction = cls.classifyInstance(i);
//			if (prediction == i.classValue()) {
//				correct++;
//			}
//		}
//		
//		double accuracy = (double)correct / data.size();
		
		boolean correct = true;
		//System.out.println("Accuracy:" + accuracy);
		
		// compare actual and predicted values
		for (Instance i : data) {
			System.out.println( 
					"actual:" + data.classAttribute().value((int)i.classValue()) +
					", predicted:" + data.classAttribute().value((int)cls.classifyInstance(i)));
			
			System.out.println("Weight of classes for each instance:");
			
			
			double[] arr =  cls.distributionForInstance(i);
			double plane = arr[0];
			double car = arr[1];
			
			if(data.classAttribute().value((int)cls.classifyInstance(i)).equals("CAR")) {
				System.out.println("Incorrect");
				correct = false;
				break;
			} else {
				System.out.println("Correct");
			}
			System.out.println("Probability it is a plane: "+   df2.format(plane));
			System.out.println("Probability it is a car: "+  df2.format(car));
			System.out.println("Probability it is a plane: "+   plane);
			System.out.println("Probability it is a car: "+  car);
			newCar=car;
			
		}

		return correct;
		
		//return accuracy;
			
	}
	
	public void filterImage() {
		
		//filter images to get attributes
		String loc = null;
		try {
			DataSource source = new DataSource(location + "/plane.arff");
			Instances dataset = source.getDataSet();
			
			//System.out.println("count: "+count);
			
			//use filter
			AbstractImageFilter filter = 
					new weka.filters.unsupervised.instance.imagefilter.SimpleColorHistogramFilter();
			filter.setInputFormat(dataset);
			
			if(count==1) {
				loc = "/image";
			} else {
				loc = "/newImage";
			}
			
			filter.setImageDirectory(location + loc);//IMAGE LOCATION
			dataset = Filter.useFilter(dataset, filter);
			
			//save filtered imah#ge file
			ArffSaver saver = new weka.core.converters.ArffSaver();
			saver.setInstances(dataset);
			saver.setFile(new File("filtered/planeFiltered.arff"));//filtered file has new attributes. Saved
			saver.writeBatch();
			
		} catch (Exception e) {
			System.out.println("Error filtering image: " + e);
		}
		
	}
	
	public void removeFilename() {
		//remove filename string so it can be classified
		 try
	        {
	            ArffLoader loader= new ArffLoader();
	            loader.setSource(new File("filtered/planeFiltered.arff"));
	            Instances data= loader.getDataSet();
	            
	            //Load Arff
	             String[] options = new String[2];
	             options[0] = "-R";                                    						// "range"
	             options[1] = "1";                                     						// first attribute
	             Remove remove = new Remove();                         						// new instance of filter
	             remove.setOptions(options);                          						// set options
	             remove.setInputFormat(data);                          						// inform filter about dataset **AFTER** setting options
	             Instances newData = Filter.useFilter(data, remove);  						// apply filter
	             
	             //save arff
	             ArffSaver saver = new ArffSaver();
	             saver.setInstances(newData);
	             saver.setFile(new File("removed/removedPlaneFiltered"+count+".arff"));
	             saver.writeBatch();	             	             
	             
		} catch (Exception e) {
			System.out.println("Error removing filename: " + e);
		}
		
	}
	
	public void updateNumber() {
		Path path = Paths.get("plane.arff");
		Charset charset = StandardCharsets.UTF_8;

		String content;
		try {
			content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll(Integer.toString(count-1), Integer.toString(count));
			Files.write(path, content.getBytes(charset));
		} catch (IOException e) {
			System.out.println("Error replacing image name: " + e);
			e.printStackTrace();
		}
		
	}
	
}