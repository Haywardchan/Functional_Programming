package ass1

/**
 * This class is a test suite for the methods in object Tree.
 *
 * To run this test suite, start "sbt" then run the "test" command.
 */
class TreeSuite extends munit.FunSuite:
  import Tree.Path
  object IllegalPathException extends java.lang.Exception 

  extension (s: String)
    def toPath: Path[String] =
      if s.isEmpty then Path("" :: Nil)
      else Path(s.split('/').toList)

  trait TestTree:
    val t = Tree[String, Int]("")
      .updated("/foo/a".toPath, 1)
      .updated("/foo/b".toPath, 2)
      .updated("/bar/a".toPath, 11)
      .updated("/bar/b".toPath, 12)
    val empty = Tree[String, Int]("")

  test("Tree.apply"){
    new TestTree:
      assertEquals(empty.toString, "Node(,List())")
    val path = "/foo/bar/baz".toPath
    val tree1 = Tree[String, Int](path)
    val payload = 42
    val tree2 = Tree[String, Int](path, payload)
    // val nulltree = Tree[String, Int](Path(Nil), payload)
    
    // Test case 1: Verify construction of a tree with an empty internal node at the given path
    assertEquals(tree1.toString,"Node(,List(Node(foo,List(Node(bar,List(Node(baz,List())))))))")
    // Test case 1: Verify construction of a tree with a single leaf at the given path and payload
    assertEquals(tree2.toString, "Node(,List(Node(foo,List(Node(bar,List(Leaf(baz,42)))))))")
    // assertThrows[IllegalPathException](nulltree.toString)
  }

  test("Tree.update with path"){
    new TestTree:
      assertEquals(empty.updated("/foo".toPath, 1).toString, "Node(,List(Leaf(foo,1)))")
      assertEquals(empty.updated("/foo/a".toPath, 1).toString, "Node(,List(Node(foo,List(Leaf(a,1)))))")
      assertEquals(empty.updated("/foo/a".toPath, 1).updated("/foo/b".toPath, 2).toString, "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2)))))")
      assertEquals(empty.updated("/foo/a".toPath, 1).updated("/foo/b".toPath, 2).updated("/bar/a".toPath, 11).toString, "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Node(bar,List(Leaf(a,11)))))")
      assertEquals(t.updated("/foo/b/c/d".toPath, 42).toString, "Node(,List(Node(foo,List(Leaf(a,1), Node(b,List(Node(c,List(Leaf(d,42))))))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))")
      assertEquals(t.toString, "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))")
  }

  test("Tree.get") {
    new TestTree:
      assertEquals(t.get("".toPath).toString, "Some(Left(List(foo, bar)))")
      assertEquals(t.get("/foo".toPath).toString, "Some(Left(List(a, b)))")
      assertEquals(t.get("/foo/a".toPath).toString, "Some(Right(1))")
      // Test case 1: Verify that an empty tree returns None
      assertEquals(empty.get("/foo".toPath), None)
      // Test case 2: Verify that a tree with one element returns the correct payload
      assertEquals(empty.updated("/foo".toPath, 1).get("/foo".toPath), Some(Right(1)))
      // Test case 3: Verify that a tree returns the correct payload for a leaf node
      assertEquals(t.get("/foo/a".toPath), Some(Right(1)))
      // Test case 4: Verify that a tree returns a list of keys for an internal node
      assertEquals(t.get("/foo".toPath), Some(Left(List("a", "b"))))
      // Test case 5: Verify that a tree returns None for a non-existent path
      assertEquals(t.get("/baz".toPath), None)
      // Test case 6: Verify that a tree returns None for a partial path
      assertEquals(t.get("/foo/c".toPath), None)
  }

  test("Tree.contains") {
    new TestTree:
      // Test case 1: Verify that an empty tree does not contain any path
      assertEquals(empty.contains("/foo".toPath), false)

      // Test case 2: Verify that a tree with one element contains the path to that element
      assertEquals(empty.updated("/foo".toPath, 1).contains("/foo".toPath), true)

      // Test case 3: Verify that a tree contains a path that has been explicitly added
      assertEquals(t.contains("/foo".toPath), true)
      assertEquals(t.contains("/foo/a".toPath), true)
      assertEquals(t.contains("/bar".toPath), true)
      assertEquals(t.contains("/bar/b".toPath), true)

      // Test case 4: Verify that a tree does not contain a path that has not been added
      assertEquals(t.contains("/baz".toPath), false)
      assertEquals(t.contains("/foo/c".toPath), false)
      assertEquals(t.contains("/bar/c".toPath), false)

      val multiLevelTree = Tree[String, Int]("")
      .updated("/level1/level2/leaf".toPath, 1)
      assertEquals(multiLevelTree.contains("/level1".toPath), true)
      assertEquals(multiLevelTree.contains("/level1/level2".toPath), true)
      assertEquals(multiLevelTree.contains("/level1/level2/leaf".toPath), true)
      assertEquals(multiLevelTree.contains("/level1/level2/leaf/extra".toPath), false)

      // Test case 6: Verify that a tree with duplicate keys returns the correct result
      val duplicateKeysTree = Tree[String, Int]("")
        .updated("/foo/a".toPath, 1)
        .updated("/foo/b".toPath, 2)
        .updated("/foo/a".toPath, 3)
      assertEquals(duplicateKeysTree.contains("/foo".toPath), true)
      assertEquals(duplicateKeysTree.contains("/foo/a".toPath), true)
      assertEquals(duplicateKeysTree.contains("/foo/b".toPath), true)
      assertEquals(duplicateKeysTree.contains("/foo/c".toPath), false)
  }

  test("Tree.flatten - Empty Tree") {
    val tree = Tree[String, Int]("")
    val flattened = tree.flatten
    assert(flattened.isEmpty)
  }
  test("Tree.flatten - Straight Tree"){
    val t_straight = Tree[String, Int]("/foo/a".toPath, 1)
    assertEquals(t_straight.flatten, List(("/foo/a".toPath, 1)))
  }

  test("Tree.flatten - 2 node Tree"){
    val t = Tree[String, Int]("/foo/a".toPath, 1).updated("/foo/b".toPath, 2)
    assertEquals(t.flatten.toString, List(("/foo/a".toPath, 1), ("/foo/b".toPath, 2)).toString())
  }

  test("Tree.flatten - 3 node Tree"){
    val t = Tree[String, Int]("/foo/a".toPath, 1).updated("/foo/b".toPath, 2).updated("/bar/a".toPath, 11)
    assertEquals(t.flatten.toString, List(("/foo/a".toPath, 1), ("/foo/b".toPath, 2), ("/bar/a".toPath, 11)).toString())
  }

  test("Tree.flatten - Tree with Leaves") {
    val tree = Tree[String, Int]("").updated("/foo/a".toPath, 1)
    .updated("/foo/b".toPath, 2).updated("/bar/a".toPath, 11).updated("/bar/b".toPath, 12)

    val expected = List(
      ("/foo/a".toPath, 1),
      ("/foo/b".toPath, 2),
      ("/bar/a".toPath, 11),
      ("/bar/b".toPath, 12)
    )
    
    val flattened = tree.flatten
    assertEquals(flattened.toString, expected.toString())
  }

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
