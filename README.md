# Lauryn Dake CSE464 Course Project Part 2

## Compilation:

run the following command:

```
mvn package
```

## Expected Outputs for Each Feature:

### Adding/Removing Nodes & Edges:
* Given an empty DotGraph called graph:
```
DotGraph graph;
```

* Adding one or more nodes:

    * To add one node "X":
    ```
    graph.addNode("X");
    ```

    * To add two or more nodes "A", "B", ... etc:
    ```
    graph.addNodes("A", "B", "C");  // adds nodes A, B, and C to the graph
    ```

* Adding an edge:
    ```
    graph.addNode("A");
    graph.addNode("B");
    graph.addEdge("A", "B");
    ```
    or
    ```
    graph.addEdge("A", "B");    // automatically adds the nodes if they don't exist in the graph
    ```

* Removing one or more nodes:

    * To remove one node "X":
    ```
    graph.removeNode("X");  // if X DNE in the graph, nothing happens
    ```
    
    * To remove two or more nodes "A", "B", ... etc:
    ```
    graph.removeNodes(new String[] {"A", B", "C"});
    ```

* Removing an edge:

    * To remove an edge between A and B
    ```
    graph.removeEdge("A", "B");
    ```


### BFS/DFS:

* Given a graph.dot file as follows:
```
digraph G {
    A -> B;
    A -> C;
    B -> D;
    C -> E;
    D -> F;
    E -> F;
}
```

* BFS
    ```
    System.out.println(graph.GraphSearch("A", "F", graph.getBFS()).toString());
    ```
    should output
    ```
    A -> B -> D -> F
    ```

* DFS
    ```
    System.out.println(graph.GraphSearch("A", "F", graph.getDFS()).toString());
    ```
    should output
    ```
    A -> C -> E -> F 
    ```

## Github Commits for Each Feature:
* Part 1 Features:
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/33eb34ac3e1f0f48384b4350a616825f714bd80d
* RemoveNode() Function 
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/aa5871a79697af094b2ffa38e0f78089b7781fdb
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/ddf0cf409a6864b37fcd0247c776cbb71953e5d6
* RemoveNodes() Function
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/4f4c169319156db0aa6a00e076c8bbe5ce9113cd
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/ddf0cf409a6864b37fcd0247c776cbb71953e5d6
* RemoveEdge() Function
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/23b6769b75ba735b073991d370fe0d7c5493986c
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/ddf0cf409a6864b37fcd0247c776cbb71953e5d6
* CI Support
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/5474a45c45ce0caa0afdb49803c4b6d62ba1b32f
* BFS Search Function
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/8943c4d3e7813e8d04fd057f0b074cbff94a756a
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/6fcd5f39cab845db2f509e867f5da32ee2aa2a19
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/297187a69f00986fb63edf47904a2bb076231e56
* DFS Search Function
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/99fb03754caf125cfd86a98060dee6824e766c12
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/633ee585b962dc3f4d734f2b2da6f492f9b68467
* Resolve Search API w/ Enum
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/2589c8a54b25c70b46598de79deb7b858803a1da
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/71198b61bf9e2707bcaa9ee6d31f17a918e89cd7
    * https://github.com/lsdake/CSE-464-2025-lsdake/commit/d54da0f4964a9affdbd225adbf070830c30aa76d
