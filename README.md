Mixtures-Of-Markov-Chains
===================

Java 8 API for the estimation of Mixtures of Markov Chains as well as standard operations like data generation,
completion and (fuzzy) clustering

A Mixture of Markov Chains (MoMC) is a probabilistic model of sequences that assume those sequences come from
difference sources. It can be described by the joint probability distribution:

```
p(tag,sequence) = p(tag) p(s1) p(s2|s1)...p(sT|sT-1)
```

for a sequence of length T, assuming a first order Markov Chain, ie, a symbol's probability is independent of all previous
history given the previous symbol.

An small dataset is provided which contains 100 sentences in 6 languages (German, Danish, Dutch, Norwegian,English,
Swedish) taken from the [Leipzig news corpus](http://corpora.uni-leipzig.de/download.html).

[This example](https://github.com/JoseLlarena/Mixtures-Of-Markov-Chains/blob/master/src/main/java/com/fluent/pgm
/mixtures/Language_Example.java) shows how MoMC clustering automatically splits the dataset into each
language (except Danish and Norwegian which due to the similarity in spelling are both given the same two tags). To
run  from root of project:

```shell
java -cp target/mixtures-1.0.0-jar com.fluent.pgm.mixtures.Language_Example src/main/resources/de_dk_nl_no_en_se.txt
src/main/resources/momc.json
```

Clustering
===================

Fuzzy clustering is implemented by computing the posterior probability of a tag given a sequence. Hard clustering
is just a case of finding the tag the highest probability (though a more efficient way to compute the latter is to use
the joint instead)


```java
        final Sequence sequence = Sequence.from_chars(untagged_string);
        final MoMC model = io.model_from(Paths.get(model_file));
        FMap<String, P> fuzzy_clustering = infer.posterior_density(sequence, model);
```


Estimation
===================

__Supervised__

```java
         MoMC model = Estimation.estimate(data_directory)
```

will read sequences, one per line, from all files in the directory, using the name of the file as the name of the tag.
Model will be estimated wth normalised counts ( Maximum Likelihood). Strictly speaking this model is a Conditional
Markov Chain rather than a mixture.


__Unsupervised__

```java
          MoMC model = Easy.mixture_from(data_file, conversion_from_string_to_Sequence, model_output_file)
```

Will read sequences and train a model using Expectation-Maximisation, saving it to the provided file and returning it.

Convergence is by default defined as an improvement of less than 99.99 % over previous iteration for 10 iterations.
The initial model has parameters initialised at random. After convergence, parameters are smoothed using a simple add
delta smoothing (adding a very small probability to all ngrams, including unseen ones)

The second argument allows specification of a conversion from the raw input string to a Sequence object. For instance
 *Sequence::from_chars* will make a Sequence where each character in the original string is a token,
 whereas Sequence:from_words will split it using white space and use the resulting chunks as tokens.

Classes [Estimation](https://github.com/JoseLlarena/Mixtures-Of-Markov-Chains/blob/master/src/main/java/com/fluent/pgm/mixtures/Estimation.java), [Initialisation](https://github.com/JoseLlarena/Mixtures-Of-Markov-Chains/blob/master/src/main/java/com/fluent/pgm/mixtures/Initialisation.java) and
[Optimisation](https://github.com/JoseLlarena/Mixtures-Of-Markov-Chains/blob/master/src/main/java/com/fluent/pgm/mixtures/Optimisation.java) can be used to implement custom EM training
schemes.



Data Completion
===================


Data completion is implemented using a simplified version of the [Viterbi algorithm](http://en.wikipedia.org/wiki/Viterbi_algorithm)

```java
        String completed = Easy.complete_characters(" with ¬¬¬ ", model_file);
```

where ¬ is used to represent the missing symbol


Data Generation
===================

Untagged data can be generated like this:

```java

    FList<Sequence> generated = Easy.untagged_data_from(model_file,output_file,1000)
```

will generate 1000 sequences and save them to file and also return them as a list.



Tagged data can be generated like this:

```java

    FList<oo<Sequence, String>> generated = Easy.tagged_data_from(model_file,output_file,1000)
```

will generate 1000 pairs of sequences with their corresponding tags and save them to file and also return them as a
list.


Performance
===================

Not great. This is due to the use of functional style operations with eager collections, ie,
chained operations do not share loops. Scalability as per algorithmic complexity
for most operations should still be ok, specifically EM is still O(kn)  (k = number of classes,
n = size of data set).

Implementation Notes (For Java Devs)
===================

Code style departs from traditional Java:

* FP-ish style with pure functions in classes as namespaces and reliance on rich collection operations (though not as
 much I'd like to due to performance reasons)

* use of under underscore instead of camel case, [see why](http://www.cs.kent.edu/~jmaletic/papers/ICPC2010-CamelCaseUnderScoreClouds.pdf)

* dropped private and final modifiers, [here's why](http://skillsmatter.com/podcast/java-jee/radical-simplicity/js-2051)


Tests are mostly integration with a few focussed unit tests. This was an experiment that's reinforced my belief that
the best way of writing tests is still mockist outside-in


Porting to other languages
====================

Get in touch if you need help to port to other programming languages.

The api is self contained except for the [P class] (https://github.com/JoseLlarena/Math-lambda/blob/master/src/main/java/com/fluent/math/P.java) (in Math-lambda) which represents the probability data type and
encapsulates underflow/overwflow handling logic. The other dependencies are there mostly to compensate for Java's lack of
a rich collections library and functional constructs.


Using as dependency
====================


You'll need [Java 8 with lambda support](http://jdk8.java.net/lambda).

To use as Maven dependency, download [jar](https://github.com/JoseLlarena/Mixtures-of-Markov-Chains/raw/master/dist/mixtures-1.0.0.jar) and run:

```shell
mvn install:install-file -DgroupId=com.fluent -DartifactId=mixtures -Dpackaging=jar -Dversion=1.0.0
-Dfile=mixtures-1.0.0.jar -DgeneratePom=true
```


then you can reference it in your pom.xml as

```xml
<dependency>
  <groupId>com.fluent</groupId>
  <artifactId>mixtures</artifactId>
  <version>1.0.0</version>
</dependency>
```

depends on [Math-lambda](https://github.com/JoseLlarena/Math-lambda),
[Utils-lambda](https://github.com/JoseLlarena/Utils-lambda) and [Fluent-Specs](https://github.com/JoseLlarena/Fluent-Specs)


Improvements
====================


* use lazy collections to improve performance
* use better Markov Chain smoothing algorithm (currently crude add delta)
* add tests
