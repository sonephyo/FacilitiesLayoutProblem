# Genetic Algorithm to solve Facilities Layout Problem

## General Overview

Facilities Layout Problem is a NP-hard optimization problem that focuses
on the optimal arrangement of the facilities in the given space.
The project aims to minimize the total transportation cost that occur 
between facilities while considering constraints such as spatial limitations
and facilities dimensions.

https://github.com/user-attachments/assets/2670b09b-2791-41a5-a672-c6ac6930c7d4

---

## Installation
**Frontend**
1. go to the directory of the frontend folder and "*npm install*"
2. run the program by "*npm run dev*"

**Backend**
1. open the backend folder using *IntellJ* IDE.
2. run the program

**Browser**
1. After completing above steps, open a browser and use the frontend url (default - [http://localhost:3000](http://localhost:3000))

---

### Guidelines
*Stations and Spots:*

- N: Number of stations (minimum of 48).
- M: Number of available spots in the two-dimensional space (M ≥ N).
- The layout can be of any shape, allowing for irregular configurations. Some spots may remain unoccupied (serving as "holes").
<br/>

*Station Types:*

- F: Number of station types (minimum of 4).
- Each station type has distinct functions and may occupy multiple adjacent spots, depending on its shape.
<br/>

*Affinity Metric:*

- Defined metric representing the affinity between any two stations based on:
- Functional relationships.
- Proximity (distance).
Maximum values based on capacity or rate.
<br/>

_Optimization Goal:_

- Maximize total affinity across all station arrangements.
<br/>

_Parallel Tasks:_

- K: At least 32 parallel tasks to explore solutions.
- Tasks should randomly swap or modify station placements and exchange solutions.
<br/>

_Computational Requirements:_

- Program must run on a computer with at least 32 cores.
<br/>

_Visualization:_

- Graphically display solutions periodically (e.g., twice per second) until convergence or a defined number of iterations.
_Convergence Criteria:_

- Terminate when convergence is reached or after a set number of iterations.

---

# Solution Overview

### Terms

**Factory** - The spots available for the stations to be placed (Note: Stations do not need to cover all the spots)

**Station** - Stations are shaped blocks that takes up the spaces of spots and carry a function and type

**ClusterStation** - ClusterStations are pairs of stations that happen to combine and work together due to placement of stations

**StationType** - StationTypes are predefined station type (Currently, four types: TypeA, TypeB, TypeC)

**Affinity Function** - The function that calculates how good the overall placement are.

## Station Placement

The four station types represents the following structure.
1) Type A - 1x1 matrix

|   | Type A |   |
|---|--------|---|
| 0 | 0      | 0 |
| 0 | 1      | 0 |
| 0 | 0      | 0 |
2) Type B - 2x1 block matrix

|   | Type B |   |
|---|--------|---|
| 0 | 2      | 0 |
| 0 | 2      | 0 |
| 0 | 0      | 0 |

3) Type C - 3x1 matrix

|   | Type C |   |
|---|--------|---|
| 0 | 3      | 0 |
| 0 | 3      | 0 |
| 0 | 3      | 0 |
4) Type D - T block matrix

|   | Type D |   |
|---|--------|---|
| 0 | 4      | 0 |
| 4 | 4      | 4 |
| 0 | 0      | 0 |


- Stations are placed in random places with the use of LocalThreadRandomizer for variation.
- If two stations happen to sit beside each other in the direction of horizontal and vertical (excluding diagonals),
the two stations will combine to form a station cluster.
(Note:_This is intentionally designed with the purpose of how combine stations can work together effectively_)
Station Cluster with higher combinations will contribute higher value when affecting the affinity function.

## Affinity Function

1. Each station/cluster_station of any type wants to be close with the station type of its next char (e.g. TypeA wants TypeB. the closer TypeA and TypeB are, higher the value of the affinity value)
2. If stations are not in a cluster or together, the closer the distance between the stations of same type, the higher the affinity value will be negatively affect.

## General Flow of the Program
1. By default, there will be 48 stations, 64 threads and 200 genetic algorithm operations (Users are able to define/modify these from the frontend)
2. Initially, 64 random factories will be created and include 48 stations in each
3. In each GA operation, the top 64 factories will be selected and will be assigned to do mutation or crossover
   1. Doing mutation includes selecting a station type and its respective stations and moving around and evaluating the affinity value (A factory has 95% chance to be selected for mutation)
   2. Doing crossover swaps one segment of the factory (Note: there are four segments in one stations divided in equal four segements) with other same segment. Then, we will go over the factories to tweak the stations that are not in their proper format (A factory has 5% chance to be selected for crossover)
4. The result list of factories will be within n and 2n given that n is the total factories before the GA operation
5. The GA operation continue until the end of the specified operation count.

## Observation
- The initial stations within the factory will be different after each operation.
  - This is due to crossover introducing new stations in the segments it is exchanging
- As operation goes on, the crossover regardless of doing operation will not be included in the selection due to mostly having lower values.
- For the default input, the operation start to converage around 400th.

## Resources

- [Flow chart Diagram](https://drive.google.com/file/d/1aNOLWbXpScuIEieXHz5GC94fuzeZIkb4/view?usp=sharing)
