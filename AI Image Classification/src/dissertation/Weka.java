package dissertation;

import java.text.DecimalFormat;

public class Weka {
	public static void main(String[] args) throws Exception{
		
		ImageProcess p = new ImageProcess("C:/Users/Kirsty/Documents/Eclipse workspace/Dissertation v4");
		
		//creates model used to classify images (only use once)
		p.createModel();
		
		
		//loop until incorrectly classified
		boolean correct = true;
		int count = 0;
		//int previousBest = 0;
		//int localOptimum = 0;
		//int limit = 0;
		DecimalFormat df2 = new DecimalFormat("#.###############");
		p.resetArff();
		p.setImgLoc("image/plane0.jpg");
		
		/****To use the Hill Climbing function,
		 *  comment out this while loop and 
		 *  change the 'count' to 'bestCount' in the getImage() method in the ProcessImage class****/

		while(correct) {
			System.out.println("\n\n\n\nImage Number: " + count);
			count++;
			p.setCount(count);
			
			//adds pixel
			p.alterImage();
			
			//makes image arff file
			p.filterImage();
			
			//removes unnecessary data from arff
			p.removeFilename();
			
			//loads pre-made model
			correct = p.loadModel();
			
			p.updateNumber();
		}
		
		
		
		/* uncomment this for simple hill climbing without adaptation for local optima*/	
		
//		while(correct) {
//		System.out.println("\n\n\n\nImage Number: " + count);
//		count++;
//		p.setCount(count);
//		
//		//adds pixel
//		p.alterImage();
//		
//		//makes image arff file
//		p.filterImage();
//		
//		//removes unnecessary data from arff
//		p.removeFilename();
//		
//		//loads pre-made model
//		correct = p.loadModel();
//		
//		
//		
//		System.out.println("best car : " + df2.format(p.getBestCar()));
//		System.out.println("new car : " + df2.format(p.getNewCar()));
//		System.out.println("bestCar: " + p.getBestCar());
//		System.out.println("newCar: " + p.getNewCar());
//		
//		
//	
//		
//		if(Double.compare(p.getNewCar(), p.getBestCar())> 0) {
//			p.setBestCar(p.getNewCar());
//			p.setBestCount(count);
//			System.out.println("new best car : " + df2.format(p.getBestCar()));
//			System.out.println("new best car : " + p.getBestCar());
//			System.out.println("new best count: " + p.getBestCount() + "\n\n\n");
//		} else {
//			localOptimum++;
//			System.out.println("best car : " + df2.format(p.getBestCar()));
//			System.out.println("best car : " + p.getBestCar());
//			System.out.println("best count: " +p.getBestCount());
//			if(localOptimum==10) {
//				p.setBestCar(p.getNewCar());
//				p.setBestCount(count);
//				localOptimum =0;
//			}
//		}
//		p.updateNumber();
//	}
		
	
		/*uncomment this for local optima hill climbing*/
		
//		p.setBestCar(0);
//		while(correct) {
//			count++;
//			p.setCount(count);
//			System.out.println("Image Number: " + count);
//
//			//changes pixel in image
//			p.alterImage();
//			
//			//makes image arff file from altered image
//			p.filterImage();
//			
//			//removes unnecessary data from arff
//			p.removeFilename();
//	
//			//loads pre-made model
//			correct = p.loadModel();
//			
//			p.updateNumber();
//			
//			System.out.println("best car : " + df2.format(p.getBestCar()));
//			System.out.println("new car : " + df2.format(p.getNewCar()));
//			System.out.println("bestCar: " + p.getBestCar());
//			System.out.println("newCar: " + p.getNewCar());
//			
//			//hill climb for best result
//			if(Double.compare(p.getNewCar(), p.getBestCar())> 0) {//the new prediction is closer to being misclassified
//				p.setBestCar(p.getNewCar());
//				p.setBestCount(count);
//				previousBest = count;
//				localOptimum = 0;
//				
//				System.out.println("new best car : " + df2.format(p.getBestCar()));
//				System.out.println("new best car : " + p.getBestCar());
//				System.out.println("new best count: " + p.getBestCount() + "\n\n\n");
//				Thread.sleep(1000);
//			
//			} else if(Double.compare(p.getNewCar(), p.getBestCar())<0){//the new image is further away from being misclassified
//				p.setBestCount(previousBest);
//				//p.setBestCar(p.getNewCar());
//				System.out.println("best car : " + df2.format(p.getBestCar()));
//				System.out.println("best car : " + p.getBestCar());
//				System.out.println("best count: " +p.getBestCount());
//				System.out.println("\t\t\t\t\t\t Backwards!\n\n\n");
//				localOptimum++;
//				Thread.sleep(1000);
//			} else {
//				p.setBestCar(p.getNewCar());
//				p.setBestCount(count);
//				limit++;
//				//previousBest = count;
//				System.out.println("same best car : " + df2.format(p.getBestCar()));
//				System.out.println("same best count: " + df2.format(p.getBestCount()) + "\n\n\n");
//			}
//			System.out.println("Previous best: "+previousBest);
//				
//			//p.info();
//			
//			if(localOptimum==5) {
//				System.out.println("\n\n\n LOCAL OPTIMUM REACHED \n\n\n");
//				Thread.sleep(3000);
//				p.setBestCar(0);
//				for(int i=0; i<20; i++) {
//					
//					count++;
//					p.setBestCount(count);
//					p.setCount(count);
//					System.out.println("Image Number local op: " + count);
//					p.alterImage();
//					p.filterImage();
//					p.removeFilename();
//					correct = p.loadModel();
//					p.setBestCar(p.getNewCar());
//					System.out.println("\n\n\n");
//					
//					
//				}
//				localOptimum=0;
//				limit++;
//			} 
//			if(limit==1000) {
//				Random r = new Random();
//				int rand = r.nextInt(count);
//				p.setBestCount(0);
//				p.setBestCar(0);
//				System.out.println("\n\n\nRESET "+0+" \n\n\n");
//				Thread.sleep(3000);
//				limit = 0;
//				
//for(int i=0; i<20; i++) {
//					
//					count++;
//					p.setBestCount(count);
//					p.setCount(count);
//					System.out.println("Image Number reset: " + count);
//					p.alterImage();
//					p.filterImage();
//					p.removeFilename();
//					correct = p.loadModel();
//					p.setBestCar(p.getNewCar());
//					System.out.println("\n\n\n");
//					
//					
//				}
//				
//			} 
//			
//			
//		}
		
		
		System.out.println("complete");
		
	}
	
}
