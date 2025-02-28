%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% File: calculateAvalanche.m
% Author: Christopher Wood, caw4567@rit.edu
% Description: Script that calculates the branch number for a 
% given S-box definition.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Change this parameter to modify the S-box behavior
SBOX_SIZE = 4;
bits = log2(SBOX_SIZE);

% Initialize the S-box matrix here
S = zeros(1, SBOX_SIZE);
indices = zeros(1, SBOX_SIZE);

% Dumbly fill in the S-box contents
for i = 1:SBOX_SIZE
	S(i) = 0; %i - 1
    indices(i) = i;
end
S(1) = 1;
S(SBOX_SIZE) = 1;

% Attempt to calculate the BN for this S-box (simple setup)
S
avalanche(S, SBOX_SIZE)

% Set up the options for the solver to make sure the interior-point 
% algorithm is used.
%options = optimset('Algorithm','interior-point','Display','iter-detailed','PlotFcns','optimplotfval');

% Invoke the fmincon function to find the minimum.
%[v1,v2] = fmincon('sboxavalancheobj',S,[],[],[],[],0,(2^SBOX_SIZE) - 1,'sboxcon',options);

% Set up for genetic algorithm
LB = 0;
UB = 2^bits - 1;
Bound = [LB;UB];
options = gaoptimset('CreationFcn', @avalanche_creation,'MutationFcn',@avalanche_mutate, ...
    'PopInitRange',Bound,'Display','iter','Generations',150,'PopulationSize',SBOX_SIZE,...
    'PlotFcns',{@gaplotbestf,@gaplotdistance});

% Run the genetic algorithm now!
[x, fval] = ga(@avalanche_fitness, SBOX_SIZE, options)