# Spark : Optimisation

## Contexte : présentation de Spark

**Spark** est un framework open source de traitement de données distribué. Il s'agit d'un ensemble d'outils et de composants logiciels développés pour fournir une alternative rapide et généralisée à MapReduce, le modèle de traitement de données associé à Apache Hadoop. Spark est conçu pour le traitement de données à grande échelle, permettant de traiter des volumes massifs de données en les distribuant sur un cluster de machines.

À l'origine, son développement visait à **accélérer le traitement des systèmes Hadoop**. Aujourd'hui, on estime qu'il est 100 fois plus rapide que ce dernier pour le traitement des données, qu'il utilise moins de ressources et propose un modèle de programmation plus simple. Mais bien que Spark puisse fonctionner très efficacement indépendamment d'Hadoop, il peut également être intégré à Hadoop YARN (Yet Another Resource Negotiator) pour partager les ressources avec d'autres frameworks Hadoop.

Une caractéristique distinctive de Spark est son **utilisation** intensive **de la mémoire vive (RAM)** pour stocker temporairement les données intermédiaires. Il effectue toutes les opérations d'analyse de données en mémoire en temps réel et utilise les disques uniquement lorsque la mémoire vive n'est pas suffisante.
Par ailleurs, Spark est connu pour utiliser un modèle de traitement basé sur les graphes acycliques dirigés pour représenter et exécuter les opérations sur les données. Cela permet une optimisation efficace des tâches par le Spark engine.

Sa facilité d'utilisation réside notamment dans le nombre d'**API de programmation** qu'il offre. Ces API sont disponibles dans plusieurs langages, dont Scala, Java, Python et R, ce qui permet aux développeurs de choisir le langage qui convient le mieux à leurs compétences et à leurs besoins.

Spark fourni également de nombreuses **bibliothèques** destinée à différent traitement de données. Elles sont rassemblées dans le tableau ci-dessous :

| outil | fonctionnalité |
| -------- | -------- |
| SQL | permet aux utilisateurs d'exécuter des requêtes SQL pour modifier et transformer des données |
| Streaming | permet un traitement de données en flux. Utilise les données en temps réel |
| Graph X | traite les informations issues de graphes |
| MLlib | bibliothèque d'apprentissage automatique contenant tous les algorithmes et utilitaires d'apprentissage classiques tels que la classification, la régression, le clustering, le filtrage collaboratif et la réduction de dimension |

L'utilisation de Spark est répandue dans de nombreux secteurs pour des applications telles que l'analyse de données, le traitement de flux en temps réel, le machine learning, et plus encore. Sa polyvalence et sa rapidité en font un choix populaire pour le traitement de données distribué à grande échelle.


## Définition : l'optimisation dans Spark

Comme indiqué dans la première partie, Spark est utilisé pour appliquer des transformations sur des données, structurées dans la plupart des cas. Il est souvent utilisé lorsque les données à traiter sont trop importantes vis-à-vis des ressources de calcul et de mémoire à disposition (big data). Ou simplement lorsque l’on veut accélérer un calcul, en mettant à contribution plusieurs machines au sein d’un même réseau. Dans les deux, l'optimisation du temps de calcul est primordiale.

Pour ce faire, l'approche la plus employée est d'augmenter les ressources allouées dans un cluster. Mais il existe des alternatives. Dans ce rapport, nous allons être amenés à présenter un aperçu de certaines des **stratégies d'optimisation** dans Spark qui permettent de réduire le temps de calcul et ce même avec des ressources limitées.

### Parallélisme

Spark puise son efficacité dans sa capacité à **traiter plusieurs tâches en parallèle**. Ainsi, plus on lui facilite le découpage des tâches, plus celles-ci seront effectuées rapidement. Pour cela, il est nécessaire de fractionner nos jeux de données en plusieurs partitions.

**Partitionner** un jeu de données signifie organiser les données en sous-ensembles configurables, facilitant ainsi une lecture parallèle et indépendante. Dans Spark, il est recommandé d'avoir un nombre de partitions au moins égal au nombre de cœurs CPU disponibles pour garantir une utilisation optimale des ressources. Cette approche permet de décomposer les étapes du traitement en un grand nombre de tâches indépendantes, assurant une exécution fluide.

Les partitions peuvent être créées directement à la lecture des données en configurant le paramètre `spark.sql.files.maxPartitionBytes` (par défaut à 128 MB) ou plus simplement dans le code à l’aide de l’API Dataframe. A travers cette API, Spark offre des transformations telles que `repartition` et `coalesce` pour ajuster le partitionnement des données.


### Placement mémoire

Spark utilise un modèle de programmation basé sur le concept de **résilience distribuée** à travers les RDDs (Resilient Distributed Datasets) et les DataFrames. En ce qui concerne la gestion de la mémoire, Spark utilise un mécanisme appelé "placement de mémoire" pour optimiser l'utilisation des ressources disponibles.

Spark utilise un gestionnaire de mémoire distribuée pour allouer et gérer la mémoire sur l'ensemble du cluster. Il utilise une approche de gestion de la mémoire en deux parties, avec une division entre la mémoire allouée aux données et celle allouée à la programmation.

De plus, afin d'améliorer les performances, une partie de la mémoire est réservée pour stocker les données en mémoire car cela évite de lire les données depuis le stockage persistant à chaque itération. Spark utilise aussi un placement de mémoire statique, où il réserve une partie de la mémoire JVM pour le stockage des données en fonction des besoins de l'application. Cela permet de minimiser le coût associé à la gestion dynamique de la mémoire. Dans une dynamique de performance, Spark utilise la mémoire cache afin de stocker des ensembles de données intermédiaires qui sont utilisés fréquemment. Ceci permet d'éviter les calculs supplémentaires.

Spark gère dynamiquement la mémoire ce qui permet d'allouer et ajuster dynamiquement la place des données en fonction des besoins de l'application. Cependant, le manque de mémoire est un enjeu crucial. Afin d'éviter les erreurs liées à une insuffisance de mémoire, le **memory spilling** permet de déplacer les données vers sur un disque tier ce qui permet d'étendre le domaine de gestion des données. 

En bref le placement de mémoire vise à maximiser l'utilisation de la mémoire disponible tout en gérant les erreurs liées à une manque de place. Cela permet d'augmenter les performances tout en assurant un fonctionnement pérenne.

### Lazy Evaluation

La **lazy evaluation**, ou évaluation différée en français, est une caractéristique importante d'Apache Spark. Cette approche consiste à retarder l'évaluation effective des transformations sur les données jusqu'à ce que l'on en ai réellement besoin - c'est à dire qu'une action soit déclenchée sur ces données. Cela signifie que lorsque l'on effectue une transformation sur un ensemble de données dans Spark, cette transformation n'est pas immédiatement exécutée. Au lieu de cela, Spark construit un plan d'exécution logique, appelé plan **DAG** (Directed Acyclic Graph), représentant les opérations à effectuer sur les données.

Cette pratique permet à Spark d'optimiser les plans d'exécution avant de réellement l'exécuter. Il peut réorganiser des opérations pour minimiser les coûts. L'évaluation différée permet à Spark d'économiser des ressources en évitant des calculs intermédiaires qui ne contribuent pas directement au résultat final.

#### Exemple

```python
# Transformation différée
data = [1, 2, 3, 4, 5]
rdd = sc.parallelize(data)
transformed_rdd = rdd.map(lambda x: x * 2)

# Aucun calcul n'a encore été effectué ici

# Déclenchement de l'évaluation
result = transformed_rdd.collect()
```

Dans cet exemple, la transformation map n'est évaluée que lorsqu'une action (collect()) est déclenchée. Cela permet à Spark d'optimiser le plan d'exécution avant de l'exécuter réellement.

La lazy evaluation est une stratégie puissante qui contribue à l'efficacité et à la performance globale d'Apache Spark, en particulier lors du traitement de grands ensembles de données distribués sur un cluster.

### Optimisation des requêtes

L'**optimisation des requêtes** dans Spark consiste à rendre les opérations sur les données aussi rapides et efficaces que possible.
Pour cela, plusieurs points doivent être considérés. L'un des plus simple à mettre en place est de **sélectionner uniquement les éléments qui seront exploités par la suite**. Par exemple, les requêtes peuvent être optimisées en limitant la projection aux seules colonnes nécessaires pour les calculs. Cela réduit la quantité de données à traiter. Imaginons que l'on dispose d'une base donnée contenant les toutes les informations associées à des utilisateurs et que l'on souhaite récupérer le nom de tous ceux âgés de plus de 21 ans, nous pourrions le faire de deux manières différentes :
````python
# Non optimisé
df_non_optimized = df.select("*").filter(df["age"] > 21)

# Optimisé
df_optimized = df.select("name").filter(df["age"] > 21)
````
Dans cet exemple, la version optimisée sélectionne uniquement les colonnes nécessaires avant d'appliquer le filtre. Cela réduit la quantité de données traitées, améliorant ainsi les performances.

Le **partitionnement des données** est également crucial dans Spark. Comme nous l'évoquions dans la partie "parallélisme", les opérations de jointure et d'agrégation sont plus efficaces lorsque les données sont correctement partitionnées. 

Une autre méthode efficace pour améliorer les performances est le **stockage temporaire** des résultats fréquemment utilisés. Il est possible d'utiliser les opérations `cache` ou `persist` pour stocker en mémoire des données fréquemment utilisées. Cela évite d'avoir à recalculer ces données lors des futures utilisations.

Par ailleurs, il est également possible d'utiliser les outils fournis par Spark, tels que la Spark Web UI pour voir à quoi ressemble l'exécution réelle du code. Cela permet d'identifier les potentiels problèmes de performances et d'ajuster accordement.

### Catalyst Optimizer

Le **Catalyst optimizer** est un optimiseur de requêtes basé sur des règles, intégré à Apache Spark. Son rôle principal est d'analyser et de transformer le plan de requête logique (exprimé en termes d'opérations sur des ensembles de données abstraits appelés DataFrames) en un plan de requête physique, qui est exécuté sur un **cluster** Spark. 

Les **règles d'optimisation** permettent de réorganiser et de réécrire le plan de requête afin de maximiser l'efficacité d'exécution. Les actions possibles sont notamment :
 - Eliminer les données ne respectant pas les conditions préalables plus tôt dans le calcul
 - Réorganiser les filtres
 - Conversion d'opérations décimales en opérations entières longues
 - Remplacement de certaines expressions RegEx par les méthodes Java startWith(String) ou contain(String)
 - Simplification des clauses if-else


#### Exemple

```sql
SELECT city, AVG(age) AS average_age
FROM users
WHERE city = 'Paris'
GROUP BY city;
```

Dans cette requête, nous filtrons d'abord les utilisateurs de la ville de Paris avant de calculer la moyenne d'âge. 
Cependant, le Catalyst optimizer pourrait réécrire cette requête de manière plus efficace en effectuant la filtration après le calcul de la moyenne. Cela pourrait être bénéfique si la ville de Paris contient un grand nombre d'utilisateurs.

```sql
SELECT city, AVG(age) AS average_age
FROM users
GROUP BY city
HAVING city = 'Paris';
```
Dans cette version optimisée, le Catalyst optimizer pourrait choisir de déplacer le critère de filtrage dans la clause `HAVING` après le calcul de la moyenne d'âge. Cela peut réduire le nombre d'enregistrements à considérer lors du calcul de la moyenne, surtout si la ville de Paris représente une petite fraction de l'ensemble des données.

L'optimisation réelle dépendrait des statistiques sur les données, de la distribution des valeurs, et d'autres facteurs spécifiques à l'environnement Spark en cours d'exécution. Le Catalyst optimizer analyse ces aspects pour générer un plan de requête physique qui minimise le coût d'exécution tout en préservant le résultat souhaité.