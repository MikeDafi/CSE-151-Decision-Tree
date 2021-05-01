import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner; // Import the Scanner class to read text files


public class DecisionTree {
	static class Node{
		double thresh = 0;
		double label = -1;
		int threshCutOffIndex = 0;
		double foundHXZ = Double.MAX_VALUE;
		int featureIndex = 0;
		List<Integer> subsetRows;
		Node left;
		Node right;
		public Node(List<Integer> rows) {
			subsetRows = rows;
		}
		public Node() {
			
		}
	}
	static ArrayList<double[]> trainingMatrix;
	static ArrayList<double[]> validationMatrix;
	static ArrayList<double[]> testMatrix;
	static ArrayList<double[]> projectionMatrix;
	public static void main(String []args){
        System.out.println("Hello World");
        Scanner trainingData = readFile("C:\\Users\\Michael\\eclipse-workspace\\CSE 151A Project2\\src\\pa2train.txt");
        Scanner testData = readFile("C:\\Users\\Michael\\eclipse-workspace\\CSE 151A Project2\\src\\pa2test.txt");
        Scanner validationData = readFile("C:\\Users\\Michael\\eclipse-workspace\\CSE 151A Project2\\src\\pa2validation.txt");
        trainingMatrix = getMatrixDouble(trainingData);
        testMatrix = getMatrixDouble(testData);
        validationMatrix = getMatrixDouble(validationData);

        ArrayList<Integer> initialRows = new ArrayList<>();
        for(int i = 0; i < trainingMatrix.size();i++) {
        	initialRows.add(i);
        }
        Node n = recursiveTree( new Node(initialRows));
        getFirstThreeLevels(n,1);
        System.out.println("training error " + getError(trainingMatrix,n));
        System.out.println("test error " + getError(testMatrix,n));
        System.out.println("AFTER PRUNING");
        pruneBFS(n);
        
	}
	
	public static void pruneBFS(Node root) {
		Node currentNode = root;
		int prunedNodes = 0;
	    Queue<Node> queue = new LinkedList<>();
	    queue.add(currentNode);
	    while(!queue.isEmpty()){
	    	currentNode = queue.remove();
	    	if(currentNode.subsetRows.size() == 0) {continue;}
			double label = findMostFrequentTrainingLabel(currentNode.subsetRows);
			double beforePruneError = getError(validationMatrix,root);
			double temp = currentNode.label;
			currentNode.label = label;
			double afterPruneError = getError(validationMatrix,root);
			System.out.println(" before " + beforePruneError + " after " + afterPruneError + " label Before " + temp + " label after " + label);
			if(afterPruneError < beforePruneError) {
				prunedNodes++;
				if(prunedNodes < 3) {
					System.out.println("Pruned " + prunedNodes + " Node. Validation error " + afterPruneError);
			        System.out.println("Pruned " + prunedNodes + " Node. Training error " + getError(trainingMatrix,root));
			        System.out.println("Pruned " + prunedNodes + " Node. Test Error " + getError(testMatrix,root));
				}
				if(prunedNodes == 3) {return;}
			}else {
				currentNode.label = temp;
			}

			if(currentNode.label == -1.0) {
				//set subsetRows
				queue.add(currentNode.left);
				queue.add(currentNode.right);
			}
			
	    }
	}
	public static double findMostFrequentTrainingLabel(List<Integer> subsetRows) {
		int count = 0;
		for(int i = 0; i < subsetRows.size();i++) {
			double label = trainingMatrix.get(subsetRows.get(i))[trainingMatrix.get(i).length - 1];
			count += label == 1.0 ? 1 : -1;
		}
		return count > 0 ? 1.0 : 0.0;
	}
	
	public static void getFirstThreeLevels(Node n,int height) {
		if(height > 3) {return;}
		
		//impure node is where label == -1
		if(n.label == -1) {
			System.out.println("--------");
			System.out.println("this node " + n + " height " + height);
			System.out.println("Feature " + (n.featureIndex + 1) + " < " + n.thresh);
			System.out.println("#training data rows " + n.subsetRows.size());
			System.out.println("left node " + n.left + " right node " + n.right);
			System.out.println("--------");
			getFirstThreeLevels(n.left,height + 1);
			getFirstThreeLevels(n.right,height + 1);
		}else {
			System.out.println("--------");
			System.out.println("this LEAF " + n);
			System.out.println("Label " + (n.label));
			System.out.println("#training data rows " + n.subsetRows.size());
			System.out.println("--------");
		}
	}
	
	public static double getError(ArrayList<double[]> dataMatrix,Node n) {
		double count = 0;
		for(int i = 0; i < dataMatrix.size();i++) {
			if((double)searchTree(dataMatrix.get(i),n) != dataMatrix.get(i)[dataMatrix.get(i).length - 1]) {
				count++;
			}
		}
		return (double)count/(double)dataMatrix.size();
	}
	
	public static double searchTree(double[] featureVector, Node n) {
		if(n.label != -1.0) {return n.label;}

		if(featureVector[n.featureIndex] < n.thresh) {
			return searchTree(featureVector,n.left);
		}else {
			return searchTree(featureVector,n.right);
		}
	}
	
	static class Pair { 
	    double colVal; 
	    int rowIndex; 
	  
	    // Constructor 
	public Pair(double colVal, int rowIndex) 
	    { 
	        this.colVal = colVal; 
	        this.rowIndex = rowIndex; 
	    } 
	} 
	  
	// class to define user defined conparator 
	static class Compare { 
	  
	    void compare(Pair arr[], int n) 
	    { 
	        // Comparator to sort the pair according to second element 
	        Arrays.sort(arr, new Comparator<Pair>() { 
	            @Override public int compare(Pair p1, Pair p2) 
	            { 
	                return (int) (p1.colVal - p2.colVal); 
	            } 
	        }); 
	  
	    } 
	} 
	  
	public static Node recursiveTree(Node currentNode) {

		//check if pure node
		boolean pureNode = true;
		for(int i = 0; i < currentNode.subsetRows.size() - 1;i++) {
			
			if(trainingMatrix.get(currentNode.subsetRows.get(i))[trainingMatrix.get(i).length - 1] != trainingMatrix.get(currentNode.subsetRows.get(i + 1))[trainingMatrix.get(i + 1).length - 1]) {
				pureNode = false;
			}
		}
		if(pureNode) {
			currentNode.label = trainingMatrix.get(currentNode.subsetRows.get(0))[trainingMatrix.get(0).length - 1];
			return currentNode;
		}

		Node bestNode = currentNode;
		for(int i = 0; i < trainingMatrix.get(0).length - 1;i++) {

			Pair[]columnValues = new Pair[currentNode.subsetRows.size()];
			for(int j = 0; j < currentNode.subsetRows.size();j++) {
				columnValues[j] = new Pair(trainingMatrix.get(currentNode.subsetRows.get(j))[i],currentNode.subsetRows.get(j));

			}
			
			Compare obj = new Compare(); 
			  
	        obj.compare(columnValues, currentNode.subsetRows.size());

			bestNode = findBestThreshold(columnValues,i,bestNode);


		}
		currentNode = bestNode;

		Pair[]columnValues = new Pair[currentNode.subsetRows.size()];
		for(int j = 0; j < currentNode.subsetRows.size();j++) {
			columnValues[j] = new Pair(trainingMatrix.get(currentNode.subsetRows.get(j))[currentNode.featureIndex],currentNode.subsetRows.get(j));
		}
		Compare obj = new Compare(); 
		
        obj.compare(columnValues, currentNode.subsetRows.size());
        ArrayList<Integer> leftRows = new ArrayList<>();
        ArrayList<Integer> rightRows = new ArrayList<>();
        
        //was very unnessecary
        for(int i = 0;i <columnValues.length;i++) {
        	if(i < currentNode.threshCutOffIndex) {
        		leftRows.add(columnValues[i].rowIndex);
        	}else {
        		rightRows.add(columnValues[i].rowIndex);
        	}
        }

		currentNode.left = new Node(leftRows);
		currentNode.right = new Node( rightRows);
		int[] leftDistribution = new int[2];
		for(int i = 0; i < currentNode.left.subsetRows.size();i++) {
			double label = trainingMatrix.get(currentNode.left.subsetRows.get(i))[trainingMatrix.get(0).length - 1];
			leftDistribution[label == 1.0 ? 1 : 0] += 1;
		}

		currentNode.left = recursiveTree(currentNode.left);
		currentNode.right = recursiveTree(currentNode.right);
		return currentNode;
	}
	
	public static Node findBestThreshold(Pair[] columnValues,int featureIndex,Node currentNode) {
		int[] leftDistribution = new int[2];
		int[] rightDistribution = new int[2];
		double labelLeft = trainingMatrix.get(columnValues[0].rowIndex)[trainingMatrix.get(0).length - 1];
		leftDistribution[labelLeft == 1.0 ? 1 : 0] += 1;
		double bestThreshold = 0;
		double minimumHXZ = Double.MAX_VALUE;
		int thresholdCutOffIndex = 0;
		for(int i = 1; i < columnValues.length;i++) {
			double label = trainingMatrix.get(columnValues[i].rowIndex)[trainingMatrix.get(0).length - 1];
			rightDistribution[label == 1.0 ? 1 : 0] += 1;
		}
		for(int i = 0; i < columnValues.length - 1;i++) {
			
			if(columnValues[i].colVal != columnValues[i+1].colVal) {
				
				double threshold = (columnValues[i].colVal + columnValues[i + 1].colVal)/2;
				double leftCondEntropy = findConditionalEntropy(leftDistribution,i + 1);
				double rightCondEntropy = findConditionalEntropy(rightDistribution,columnValues.length - (i + 1));
				double probFirst = (double)(i + 1)/(double)columnValues.length;
				double probSecond = (double)(columnValues.length - (i + 1))/(double)columnValues.length;
				double first = probFirst * leftCondEntropy;
				double second = probSecond * rightCondEntropy;
				
				double hXZ = (double)first + (double)second;
				bestThreshold = hXZ < minimumHXZ ? threshold : bestThreshold;
				thresholdCutOffIndex = hXZ < minimumHXZ ? (i + 1) : thresholdCutOffIndex;
				minimumHXZ = Math.min(hXZ, minimumHXZ);

			}
			double label = trainingMatrix.get(columnValues[i+1].rowIndex)[trainingMatrix.get(0).length - 1];
			leftDistribution[label == 1.0 ? 1 : 0] += 1;
			rightDistribution[label == 1.0 ? 1 : 0] -= 1;
		}
		if(currentNode.foundHXZ > minimumHXZ) {
			currentNode.thresh = bestThreshold;
			currentNode.foundHXZ =  minimumHXZ;
			currentNode.featureIndex = featureIndex;
			currentNode.threshCutOffIndex = thresholdCutOffIndex;;
			
		}
		return currentNode;
	}
	
	public static double findConditionalEntropy(int[] distribution,int n) {
		double conditionalEntropy = 0;
        double prob0 =(double) ((double)(distribution[0]) / n);
        double prob1 =(double) ((double)(distribution[1]) / n);    
        double log0 = prob0 == 0.0 ? 0.0 : Math.log10(prob0)/Math.log10(2);
        double log1 = prob1 == 0.0 ? 0.0 : Math.log10(prob1)/Math.log10(2);
        conditionalEntropy = (double)(-1 * prob0 * log0) - (double)(prob1 * log1) ;
        return conditionalEntropy;
	}
	
 	public static ArrayList<double[]> getMatrixDouble(Scanner data) {
		ArrayList<double[]> matrix = new ArrayList<>();
		while(data.hasNextLine()) {
			String dataString = data.nextLine();
			String[] dataArray = dataString.split(" ");
			double[] n1 = new double[dataArray.length];
			for(int i = 0; i < dataArray.length; i++) {
			   n1[i] = Double.parseDouble(dataArray[i]);
			}
			matrix.add(n1);
			
		}
		return matrix;
	}
	
	public static ArrayList<int[]> getMatrixInt(Scanner data) {
		ArrayList<int[]> matrix = new ArrayList<>();
		while(data.hasNextLine()) {
			String dataString = data.nextLine();
			String[] dataArray = dataString.split(" ");
			int[] n1 = new int[dataArray.length];
			for(int i = 0; i < dataArray.length; i++) {
			   n1[i] = Integer.parseInt(dataArray[i]);
			}
			matrix.add(n1);
			
		}
		return matrix;
	}
	
	
	public static Scanner readFile(String fileName) {
		try {
		      File myObj = new File(fileName);
		      Scanner myReader = new Scanner(myObj);
		      return myReader;
		}
		catch(FileNotFoundException e) {
			System.out.println("An error occured.");
			e.printStackTrace();
			return null;
		}
	}
	
	
}
