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

An example dataset is provided which contains 100 sentences in 6 languages (German, Danish, Dutch, Norwegian,English,
Swedish) taken from the [Leipzig news corpus](http://corpora.uni-leipzig.de/download.html).

[This example](https://github.com/JoseLlarena/Mixtures-Of-Markov-Chains/blob/master/src/main/java/com/fluent/pgm/mixtures/Language_Example.java) shows how MoMC clustering automatically splits the dataset into each language,
except Danish and Norwegian which due to the similarity in spelling are both given the same two tags.To run from root
 of project:

```shell
java -cp target/mixtures-1.0.0-jar com.fluent.pgm.mixtures.Language_Example src/main/resources/de_dk_nl_no_en_se.txt
src/main/resources/momc.json
```

Clustering
===================

Fuzzy clustering is implemented by computing the posterior probability of a tag given a sequence. Hard clustering
is just a case of finding the tag the highest probability


```java
        final Sequence sequence = Sequence.from_chars(untagged_string);
        final MoMC model = io.model_from(Paths.get(model_file));
        FMap<String, P> fuzzy_clustering = infer.posterior_density(sequence, model);
```


Estimation
===================

TBD

Data Completion
===================

TBD

Data Generation
===================

TBD


Performance
===================

Not great. This is due to the use of non-lazy collections, ie, chained operations do not share loops. Big O complexity
for most operations should still be best, specifically EM is still O(kn)  (k = number of classes,
n = size of data set).

Implementation Notes (For Java Devs)
===================

Code style departs from traditional Java:

* use of under underscore instead of camel case, see [why](http://www.cs.kent.edu/~jmaletic/papers/ICPC2010-CamelCaseUnderScoreClouds.pdf)

* dropped private and final modifiers, here's [why](http://skillsmatter.com/podcast/java-jee/radical-simplicity/js-2051)


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
