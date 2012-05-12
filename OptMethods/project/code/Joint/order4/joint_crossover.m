function pop = bn_crossover(parents, options, GenomeLength, FitnessFcn, state, thisScore, thisPopulation)
%AVALANCHE_CREATION Summary of this function goes here
%   Detailed explanation goes here

SBOX_SIZE = options.PopulationSize;
range = options.PopInitRange;
lower = range(1,:);
span = range(2,:) - lower;

pop = repmat(lower, SBOX_SIZE,1) + ...
    round(repmat(span, SBOX_SIZE,1) .* rand(SBOX_SIZE, GenomeLength));

% Rotate for additional randomness
%for i = 1:mutationPop
%   children(i) = mod(children(i) + mutationPop, mutationPop); 
%end

end

