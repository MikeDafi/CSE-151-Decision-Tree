# CSE-151-Decision-Tree
In this problem, we will look at the task of classifying whether a client is likely to default on their credit
card payment based on their past behaviour and other characteristics. We will use a decision tree for this
purpose.
Download the files pa2train.txt, pa2validation.txt and pa2test.txt from the class website. These
are your training, validation and test sets respectively. The files are in ASCII text format, and each line
of the file contains a feature vector followed by its label. Each feature vector has 22 coordinates; they are
named Feature 1, Feature 2, . . . , Feature 22, respectively. The coordinates are separated by spaces. The
last (23rd) coordinate represents the label of an example, that is, whether the card-holder defaults on their
credit card bill in October, where 1 means yes, and 0 means no.
1. First, build an ID3 Decision Tree classifier based on the data in pa2train.txt. Do not use pruning.
Draw the first three levels decision tree that you obtain. For each node that you draw, if it is a leaf
node, write down the label that will be predicted for this node, as well as how many of the training
data points lie in this node. If it is an internal node, write down the splitting rule for the node, as well
as how many of the training data points lie in this node. (Hint: If your code is correct, the root node
will involve the rule Feature 5 < 0.5.)
2. What is the training and test error of your classifier in part (1), where test error is measured on the
data in pa2test.txt?
3. Now, prune the decision tree developed in part (1) using the data in pa2validation.txt. While
selecting nodes to prune, select them in Breadth-First order, going from left to right (aka, from the
Yes branches to the No branches). Write down the validation and test error after 1 and 2 rounds of
pruning (that is, after you have pruned 1 and 2 nodes from the tree.)
4. Download the file pa2features.txt from the class website. This file provides a description in order of
each of the features – that is, it tells you what each coordinate means. Based on the feature descriptions,
what do you think is the most salient or prominent feature that predicts credit card default? (Hint:
More salient features should occur higher up in the ID3 Decision tree.)
