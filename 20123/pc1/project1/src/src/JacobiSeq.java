/**
 * This class implements a sequential version of the Jacobi 
 * algorithm for solving a system of linear equations.
 * 
 * @author Christopher Wood, caw4567@rit.edu
 */

import java.io.IOException;
import edu.rit.pj.Comm;
import edu.rit.util.Random;

public class JacobiSeq 
{	
	// The convergence cutoff delta value.
	private static double epsilon = 0.00000008;

	/**
	 * The main entry point for the JacobiSeq program.
	 * 
	 * @param args - command line arguments
	 * 
	 * Note: The command-line parameters must specify the 
	 * the matrix dimension and a random seed. Also,
	 * the JVM heap size must be increased to 2000MB to
	 * support allocating such large matrices. See
	 * the usage description below for more details.
	 * 
	 * Usage:
	 * 
	 *   java -Xmx2000m JacobiSeq <n> <seed>
	 */
	public static void main(String[] args) 
	{
		// Set up the communication with the job server.
		try 
		{
			Comm.init(args);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Verify the command-line arguments. 
		if (args.length != 2) 
		{
			showUsage();
		}
		
		// Parse the command line arguments
		long start = System.currentTimeMillis();
		try 
		{
			final int n = Integer.parseInt(args[0]);
			final long seed = Long.parseLong(args[1]);
			
			// Create a random matrix
		    final double[][] A = new double[n][n];
		    final double[] b = new double[n];
		    Random prng = Random.getInstance(seed);
		    for (int i = 0; i < n; i++) 
		    {
		        for (int j = 0; j < n; j++) 
		        {
		        	A[i][j] = (prng.nextDouble() * 9.0) + 1.0;
		        }
		        A[i][i] += 10.0 * n;
		        b[i] = (prng.nextDouble() * 9.0) + 1.0;
	        }
		    
		    // Solve the system and display the solution.
		    double[] x = solve(A, b, n);
		    printSolution(start, x, n);
		} 
		catch (NumberFormatException ex1) 
		{
			System.err.println("Error parsing command line arguments.");
			ex1.printStackTrace();
		}
	}
	
	/**
	 * Display the solution vector and program runtime.
	 * 
	 * @param start - start time for program run
	 * @param x[] - the solution vector
	 * @param n - the length of the solution vector.
	 */
	public static void printSolution(long start, double[] x, int n) 
	{
	    if (n <= 100)
	    {
	    	for (int i = 0; i < n; ++ i)
	    	{
	        	System.out.printf("%d %g%n", i, x[i]);
	    	}
	    }
	    else
	    {
	    	for (int i = 0; i <= 49; ++ i)
	    	{
	        	System.out.printf("%d %g%n", i, x[i]);
	    	}
	        for (int i = n - 50; i < n; ++ i)
	        {
	        	System.out.printf("%d %g%n", i, x[i]);
	        }
	    }
	    long end = System.currentTimeMillis();
	    System.out.printf("%d msec%n", (end - start)); 
	}
	
	/**
	 * Attempt to solve the system of linear equations defined by Ax = b,
	 * where x is the solution vector.
	 * 
	 * @param A - the matrix of system coefficients
	 * @param b - the vector of system equation results
	 * @param n - the dimension of the solution vector
	 * 
	 * @return x[] - the solution vector
	 */
	public static double[] solve(double[][] A, double[] b, int n) 
	{	
		// Allocate space for the solution and temporary variables
		double[] x = new double[n];
		double[] y = new double[n];
		
		// Initialize the x[] vector to 1
	    for (int i = 0; i < n; i++) 
	    {
	    	x[i] = 1.0;
	    }
	    
	    // Run until we converge to a solution.
	    boolean converged = false;
	    boolean iterSuccess;
	    double sum1;
    	double sum2;
	    while (!converged) 
	    {
	    	iterSuccess = true;
		    for (int i = 0; i < n; i++)
		    {
		    	// Compute the upper and lower matrix product, omitting
		    	// the element at index i
		    	double[] A_i = A[i];
		    	double yVal = 0.0;
		    	double xVal = x[i];
		    	sum1 = sum2 = 0.0;
		    	for (int j = 0; j < i; j++)
		    	{
		    		sum1 += (A_i[j] * x[j]);
		    	}
		    	for (int j = i + 1; j < n; j++)
		    	{
		    		sum2 += (A_i[j] * x[j]);
		    	}
		    	
		    	// Compute and store the y[] value
		    	yVal = (b[i] - sum1 - sum2) / A_i[i];
		    	y[i] = yVal;
		    	
		    	// Check for convergence.
		    	if (iterSuccess && !(Math.abs((2 * (xVal - yVal)) / 
		    			(xVal + yVal)) < epsilon)) 
		    	{
		    		iterSuccess = false;
		    	}
		    }
		    
		    // Swap the x[] and y[] vectors.
		    double[] tmp = x;
		    x = y;
		    y = tmp;
		    
		    // Reset the iteration variables.
		    converged = iterSuccess;
		    iterSuccess = true;
	    }
		
		return x;
	}
	
	/**
	 * Display the program usage message and terminate abnormally.
	 * 
	 * @param none
	 * @return void
	 */
	public static void showUsage() 
	{
		System.err.println("Usage: java -Xmx2000m JacobiSeq <n> <seed>");
		System.exit(-1);
	}
}
