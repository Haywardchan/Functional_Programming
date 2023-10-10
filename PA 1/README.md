## Changelog

### 2022/10/03

- Fixed description of the left case of `Tree.get`.
- Added exception cases to `Tree.apply`.
- Added FAQ for testing exceptions.

# Assignment 1: Trees

The goal of this assignment is to implement a minimal data structure for trees.
The motivating example for such trees is a file system,
which is represented as a tree of directories and files.
Although we would be implementing a generic tree,
it would still be instructive to think of the file system example.

## Representation

The `Tree` type is parametrized by `K` and `V`, which stand for "key" and "value".
Each node of a tree carries a key of type `K`.

We classify nodes of the tree into two disjoint kinds:

- _Internal nodes_ have a number (possibly zero) of childrens, which are themselves trees,
  with distinct keys among siblings.
  They are represented as the type `Tree.Node`.
  Their children are represented as a list of trees.
- _Leaves_ have a payload of type `V` attached.
  They are represented as the type `Tree.Leaf`.

Nodes are identified by their paths, i.e. the lists of ancestors from the root to them.
Paths are represented as a `List` of `K`, wrapped inside the type `Tree.Path[K]`.

## Methods

For illustrative purpose, consider the following tree `t`:
```
Node(, ...)
├── Node(foo, ...)
│   ├── Leaf(a, 1)
│   └── Leaf(b, 2)
└── Node(bar, ...)
    ├── Leaf(a, 11)
    └── Leaf(b, 12)
```

### `Tree.contains`

```scala
def contains(path: Path[K]): Boolean
```
Returns `true` if the tree contains the path `path`,
regardless of whether the path refers to an internal node or a leaf,
and `false` otherwise.

### `Tree.get`

```scala
def get(path: Path[K]): Option[Either[List[K], V]]
```
Optionally returns the content at the path `path`, wrapped in an `Either`. In other words:

- Returns a `None` if the tree does not contain `path`.
- Returns a `Some(Left(ls))` if `path` refers to an internal node with `children` as its children,
  where `ls` is a list containing the keys of the elements in `children`.
- Returns a `Some(Right(payload))` if `path` refers to a leaf with `payload` as its payload.

### `Tree.flatten`

```scala
def flatten: List[(Path[K], V)]
```
Returns a list of pairs of paths and values of all leaves in the tree.

For example, `t.flatten` would return the following list:
```
List(
    (Path(List(, foo, a)),1),
    (Path(List(, foo, b)),2),
    (Path(List(, bar, a)),11),
    (Path(List(, bar, b)),12)
)
```

### `Tree.updated`

```scala
def updated(path: Path[K]): Tree[K, V]
```
Returns a tree with a new empty internal node at the path `path`.
If the path passes through an existing internal node, add to its children.
If the path passes through an existing leaf, replace it with an internal node (discarding its payload).
If the path refers to an existing node, replace it.
If the path is empty or does not start at the root, throw `Tree.IllegalPathException`.

For example, `t.updated(Path(List("", "foo", "b", "c", "d")))` would return the following tree:
```
Node(, ...)
├── Node(foo, ...)
│   ├── Leaf(a, 1)
│   └── Node(b, ...)
│       └── Node(c, ...)
│           └── Node(d, ...)
└── Node(bar, ...)
    ├── Leaf(a, 11)
    └── Leaf(b, 12)
```

While `t.updated(Path(List("", "bar")))` would return:
```
Node(, ...)
├── Node(foo, ...)
│   ├── Leaf(a, 1)
│   └── Leaf(b, 2)
└── Node(bar, ...)
```

`t.updated(Path(List()))` and `t.updated(Path(List("a", "b")))` would throw `Tree.IllegalPathException`.

```scala
def updated(path: Path[K], payload: V): Tree[K, V]
```
Returns a tree with a new leaf with payload `payload` at the path `path`.
If the path passes through an existing internal node, add to its children.
If the path passes through an existing leaf, replace it with an internal node (discarding its payload).
If the path refers to an existing node, replace it.
If the path is empty or does not start at the root, throw `Tree.IllegalPathException`.

For example, `t.updated(Path(List("", "foo", "b", "c", "d")), 42)` would return the following tree:
```
Node(, ...)
├── Node(foo, ...)
│   ├── Leaf(a, 1)
│   └── Node(b, ...)
│       └── Node(c, ...)
│           └── Leaf(d, 42)
└── Node(bar, ...)
    ├── Leaf(a, 11)
    └── Leaf(b, 12)
```

While `t.updated(Path(List("", "bar")), 0)` would return:
```
Node(, ...)
├── Node(foo, ...)
│   ├── Leaf(a, 1)
│   └── Leaf(b, 2)
└── Leaf(bar, 0)
```

`t.updated(Path(List()), 0)` and `t.updated(Path(List("a", "b")), 0)` would throw `Tree.IllegalPathException`.

## Companion object methods

The companion object provides a few constructors for `Tree`.

```scala
def apply[K, V](key: K): Tree[K, V]
```
Constructs a tree with an empty internal node with key `key` as its root.

```scala
def apply[K, V](path: Path[K]): Tree[K, V]
```
Constructs a tree with an empty internal node at path `path`.
Throws `Tree.IllegalPathException` if called with an empty path.

```scala
def apply[K, V](path: Path[K], payload: V): Tree[K, V]
```
Constructs a tree with a single leaf with payload `payload` at path `path`.
Throws `Tree.IllegalPathException` if called with an empty path.

## A note on the order of lists

The order of children in the internal representation of `Node` does not matter,
as seen by the `private` modifier in the cases of `Tree`,
which forbids anyone outside the class from accessing underlying representations.

For functions returning lists, their orders also do not matter.
We will only grade the contents of the lists.

## Tests

A few simple tests are provided in the test suite,
but they are far from covering every cases.
So you are encouraged to write your own tests to test your code!

## Submission

Submit `Tree.scala` only to the canvas assignment by __Oct 20__.

## Hint

For `Tree.updated`, one possible implementation would be
to peek at the next segment in the path to determine which child to recurse on,
which is quite easy to perform by pattern matching.
Alternatively, you can also try to catch an exception,
but this may not be the cleanest solution.

## FAQ

- **Writing tests to intercept an exception**</br>
  To write a test in MUnit to intercept an exception,
  the type of the exception must be supplied as a type argument.
  Using `Tree.IllegalPathException` would not work,
  since it refers to the class `Tree.IllegalPathException`, which is not declared.
  To refer to the type of the object `Tree.IllegalPathException`,
  use `Tree.IllegalPathException.type` instead.
