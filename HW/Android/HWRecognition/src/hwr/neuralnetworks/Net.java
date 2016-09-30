package hwr.neuralnetworks;

import hwr.database.Table;
import hwr.signal.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.os.Environment;

public class Net {
	public static String FOLDER = "/Download/HANDWRITING/NEURALNETWORKS/";
	static double LEARNING_RATE = 0.01;
	static double MOMENTUM = 0.7;
    public static int nInput, nHidden, nOutput;

    //neurons
    public static double[] inputNeurons;
    public static double[] hiddenNeurons;
    public static double[] outputNeurons;

    //weights
    public static double[][] wInputHidden;
    public static double[][] wHiddenOutput;

    //change to weights
    public static double[][] deltaInputHidden;
    public static double[][] deltaHiddenOutput;

    //error gradients
    public static double[] hiddenErrorGradients;
    public static double[] outputErrorGradients;

    //learning parameters
    public static double learningRate;
    public static double momentum;
    
    public static double mse;
    public static double accuracy;
    
    public static void initNet(int nI, int nH, int nO)
    {
        nInput = nI;
        nHidden = nH;
        nOutput = nO;

        inputNeurons = new double[nInput];
        for (int i = 0; i < nInput; i++)
        {
            inputNeurons[i] = 0;
        }
        hiddenNeurons = new double[nHidden];
        for (int i = 0; i < nHidden; i++)
        {
            hiddenNeurons[i] = 0;
        }
        outputNeurons = new double[nOutput];
        for (int i = 0; i < nOutput; i++)
        {
            outputNeurons[i] = 0;
        }
        wInputHidden = new double[nInput][];
        for (int i = 0; i < nInput; i++)
        {
            wInputHidden[i] = new double[nHidden];
            for (int j = 0; j < nHidden; j++)
            {
                wInputHidden[i][j] = 0;
            }
        }
        wHiddenOutput = new double[nHidden][];
        for (int i = 0; i < nHidden; i++)
        {
            wHiddenOutput[i] = new double[nOutput];
            for (int j = 0; j < nOutput; j++)
            {
                wHiddenOutput[i][j] = 0;
            }
        }

        deltaInputHidden = new double[nInput][];
        for (int i = 0; i < nInput; i++)
        {
            deltaInputHidden[i] = new double[nHidden];
            for (int j = 0; j < nHidden; j++)
            {
                deltaInputHidden[i][j] = 0;
            }
        }
        deltaHiddenOutput = new double[nHidden][];
        for (int i = 0; i < nHidden; i++)
        {
            deltaHiddenOutput[i] = new double[nOutput];
            for (int j = 0; j < nOutput; j++)
            {
                deltaHiddenOutput[i][j] = 0;
            }
        }

        hiddenErrorGradients = new double[nHidden];
        for (int i = 0; i < nHidden; i++)
        {
            hiddenErrorGradients[i] = 0;
        }
        outputErrorGradients = new double[nOutput];
        for (int i = 0; i < nOutput; i++)
        {
            outputErrorGradients[i] = 0;
        }
        learningRate = LEARNING_RATE;
        momentum = MOMENTUM;
        mse = 0;
        accuracy = 0;
    }
    public static void initializeWeights()
    {
    	double rH = 1.0/Math.sqrt( (double) nInput);
    	double rO = 1.0/Math.sqrt( (double) nHidden);
        Random random = new Random();
        for (int i = 0; i < nInput; i++)
        {
            for (int j = 0; j < nHidden; j++)
            {
            	wInputHidden[i][j] = random.nextDouble()*2*rH - rH;
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            for (int j = 0; j < nOutput; j++)
            {
            	wHiddenOutput[i][j] = random.nextDouble()*2*rO - rO;
            }
        }
    }
    public static void setLearningParameters(double lr, double m)
    {
    	learningRate = lr;
    	momentum = m;
    }
    public static double activationFunction(double x)
    {
        return (double)(1.0 / (1 + Math.exp(-x)));
    }
    public static void feedForward(double[] inputs)
    {
        for (int i = 0; i < nInput; i++)
        {
        	inputNeurons[i] = inputs[i];
        }
        for (int i = 0; i < nHidden; i++)
        {
        	hiddenNeurons[i] = 0;
            for (int j = 0; j < nInput; j++)
            {
            	hiddenNeurons[i] += inputNeurons[j] * wInputHidden[j][i];
            }
            hiddenNeurons[i] = activationFunction(hiddenNeurons[i]);
        }
        for (int i = 0; i < nOutput; i++)
        {
        	outputNeurons[i] = 0;
            for (int j = 0; j < nHidden; j++)
            {
            	outputNeurons[i] += hiddenNeurons[j] * wHiddenOutput[j][i];
            }
            outputNeurons[i] = activationFunction(outputNeurons[i]);
        }
    }
    public static void backpropagate(double[] desiredValues)
    {
        double sum;
        for (int i = 0; i < nOutput; i++)
        {
        	mse += Math.pow(desiredValues[i] - outputNeurons[i], 2);
        	outputErrorGradients[i] = outputNeurons[i] * (1 - outputNeurons[i]) * (desiredValues[i] - outputNeurons[i]);
            for (int j = 0; j < nHidden; j++)
            {
            	deltaHiddenOutput[j][i] = learningRate * hiddenNeurons[j] * outputErrorGradients[i] + momentum * deltaHiddenOutput[j][i];
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            sum = 0;
            for (int j = 0; j < nOutput; j++)
            {
                sum += outputErrorGradients[j] * wHiddenOutput[i][j];
            }
            hiddenErrorGradients[i] = hiddenNeurons[i] * (1 - hiddenNeurons[i]) * sum;
            for (int j = 0; j < nInput; j++)
            {
            	deltaInputHidden[j][i] = learningRate * inputNeurons[j] * hiddenErrorGradients[i] + momentum * deltaInputHidden[j][i];
            }
        }
    }
    public static void updateWeights()
    {
        for (int i = 0; i < nInput; i++)
        {
            for (int j = 0; j < nHidden; j++)
            {
            	wInputHidden[i][j] += deltaInputHidden[i][j];
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            for (int j = 0; j < nOutput; j++)
            {
            	wHiddenOutput[i][j] += deltaHiddenOutput[i][j];
            }
        }
    }
    public static int[] messyArray(ArrayList<Integer> list, Random random)
	{
		int result[] = new int[list.size()];
		ArrayList<Integer> clone = new ArrayList<Integer>(list);
		for(int i=0; i<result.length; i++)
		{
			int tmp = random.nextInt(clone.size());
			result[i] = clone.get(tmp);
			clone.remove(tmp);
		}
		return result;
	}
    public static void trainNetworks(Random random, ArrayList<Vector> vecTrain, ArrayList<Vector> vecTest)
    {
    	ArrayList<Integer> order = new ArrayList<Integer>();
    	for(int i=0; i<vecTrain.size(); i++)
    	{
    		order.add(i);
    	}
    	int arr[] = messyArray(order, random);
    	
    	mse = 0;
    	for(int i=0; i<vecTrain.size(); i++)
    	{
    		feedForward(vecTrain.get(arr[i]).features);
            backpropagate(vecTrain.get(arr[i]).target);
            updateWeights();
    	}
    	mse = (double) mse/ (double)vecTrain.size();
    	
        int incorrectResults = 0;
		for(int i=0; i<vecTest.size(); i++)
		{
			feedForward(vecTest.get(i).features);
            getRoundedOutputValue();
            if (compare(outputNeurons, vecTest.get(i).target))
                incorrectResults++;
		}
		accuracy = (double) incorrectResults*100/ (double) vecTest.size();
    }
    public static void trainNetworks(Random random, ArrayList<Vector> vecTrain)
    {
    	ArrayList<Integer> order = new ArrayList<Integer>();
    	for(int i=0; i<vecTrain.size(); i++)
    	{
    		order.add(i);
    	}
    	int arr[] = messyArray(order, random);
    	
    	mse = 0;
    	for(int i=0; i<vecTrain.size(); i++)
    	{
    		feedForward(vecTrain.get(arr[i]).features);
            backpropagate(vecTrain.get(arr[i]).target);
            updateWeights();
    	}
    	mse = (double) mse/ (double)vecTrain.size();
    }
    public static void getRoundedOutputValue()
    {
        for (int i = 0; i < nOutput; i++)
        {
            if (outputNeurons[i] < 0.2)
                outputNeurons[i] = 0.0;
            else if (outputNeurons[i] > 0.8)
                outputNeurons[i] = 1.0;
            else
                outputNeurons[i] = -1.0;
        }
    }
    public static boolean compare(double[] a, double[] b)
    {
        if (a.length != b.length)
            return false;
        else
        {
            for (int i = 0; i < a.length; i++)
            {
                if (a[i] != b[i])
                    return false;
            }
            return true;
        }
    }
    public static String detect(double[] input)
    {
    	feedForward(input);
        getRoundedOutputValue();
        for(int i=0; i<Table.list.size(); i++)
        {
        	if(compare(outputNeurons, Table.list.get(i).target))
        	{
        		return Table.list.get(i).value;
        	}
        }
        return "*";
    }
    public static void loadNetworks(String file) throws IOException
    {
    	String path = FOLDER + file+".txt";
    	ArrayList<String> lines = readFiles(path);
        String[] tmp1 = lines.get(1).split("\t");
        nInput = Integer.parseInt(tmp1[0]);
        nHidden = Integer.parseInt(tmp1[1]);
        nOutput = Integer.parseInt(tmp1[2]);
        initNet(nInput, nHidden, nOutput);
        for (int i = 0; i < nInput; i++)
        {
            String[] tmp2 = lines.get(3 + i).trim().split("\t");
            for (int j = 0; j < nHidden; j++)
            {
                wInputHidden[i][j] = Double.parseDouble(tmp2[j]);
            }
        }
        for (int i = 0; i < nHidden; i++)
        {
            String[] tmp3 = lines.get(4 + nInput + i).trim().split("\t");
            for (int j = 0; j < nOutput; j++)
            {
                wHiddenOutput[i][j] = Double.parseDouble(tmp3[j]);
            }
        }
        String[] tmp4 = lines.get(5 + nInput + nHidden).split("\t");
        learningRate = Double.parseDouble(tmp4[0]);
        momentum = Double.parseDouble(tmp4[1]);
    }
    public static void saveNetworks(String file) throws Exception 
    {
    	StringBuilder content = new StringBuilder();
    	content.append("nInput\tnHidden\tnOutput\n");
    	content.append(Integer.toString(nInput) + "\t" + Integer.toString(nHidden) + "\t" + Integer.toString(nOutput) + "\n");
    	content.append("Weights Input Hidden\n");
        for (int i = 0; i < nInput; i++)
        {
            for (int j = 0; j < nHidden; j++)
            {
            	content.append(Double.toString(wInputHidden[i][j]) + "\t");
            }
            content.append("\n");
        }
        content.append("Weights Hidden Output\n");
        for (int i = 0; i < nHidden; i++)
        {
            for (int j = 0; j < nOutput; j++)
            {
            	content.append(Double.toString(wHiddenOutput[i][j]) + "\t");
            }
            content.append("\n");
        }
        content.append("LearningRate\tMomentum\n");
        content.append(Double.toString(learningRate) + "\t" + Double.toString(momentum));
    	
        File sdcard = Environment.getExternalStorageDirectory();
        String path = FOLDER + file+".txt";
		File fo = new File(sdcard, path);
        if (!fo.exists()) {
            fo.createNewFile();
        }
		FileWriter fw = new FileWriter(fo.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content.toString());
        bw.close();
        fw.close();
    }
    public static ArrayList<String> readFiles(String fileName)
	{
		ArrayList<String> result = new ArrayList<String>();
		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard,fileName);
        try {
        	FileReader fr = new FileReader(file.getAbsoluteFile());
    	    BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
            fr.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
        return result;
	}
}
