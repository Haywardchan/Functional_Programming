package ass1

/**
 * This class is a test suite for the methods in object Tree.
 *
 * To run this test suite, start "sbt" then run the "test" command.
 */
class TreeSuite2 extends munit.FunSuite:
  import Tree.Path

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

  def plsfail(f: () => Any): Boolean = {
    try {
      f()
    }
    catch {
      case _ => return true;
    }
    false
  }

  test("Sample tree") {
    new TestTree:
      assertEquals(t.toString, "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))")
  }

  test("Tree.updated") {
    new TestTree:
      assertEquals(t.updated("/foo/b/c/d".toPath, 42).toString, "Node(,List(Node(foo,List(Leaf(a,1), Node(b,List(Node(c,List(Leaf(d,42))))))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))")
  }

  test("Get on an empty tree should return None") {
    val tree = Tree[String, Int]("")
    val result = tree.get(Tree.Path(List("foo")))
    assertEquals(result, None)
  } // D O N E

  test("Tree.get") {
    new TestTree:
      assertEquals(t.get("".toPath).toString, "Some(Left(List(foo, bar)))")
  }

  test("Get existing leaf node") {
    new TestTree {
      val result = t.get("/foo/a".toPath)
      assertEquals(result, Some(Right(1)))
    }
  }

  test("Get existing internal node") {
    new TestTree {
      val result = t.get("/foo".toPath)
      assertEquals(result, Some(Left(List("a", "b"))))
    }
  }

  test("Get non-existing path") {
    new TestTree {
      val result = t.get("/ass".toPath)
      assertEquals(result, None)
    }
  } // D O N E

  // This is UB
  test("Get with empty path") {
    new TestTree {
      val result = t.get(Tree.Path(Nil))
      assertEquals(result, None)
    }
  } // D O N E

  test("Get root") {
    new TestTree {
      val result = t.get("".toPath)
      assertEquals(result, Some(Left(List("foo", "bar"))))
    }
  }

  test("Get nonexisting path 2") {
    new TestTree {
      val result = t.get("/foo/c".toPath)
      assertEquals(result, None)
    }
  } // D O N E

  test("Flatten tree with multiple leaves") {
    new TestTree {
      val result = t.flatten
      val expected = List(
        (Tree.Path(List("", "foo", "a")), 1),
        (Tree.Path(List("", "foo", "b")), 2),
        (Tree.Path(List("", "bar", "a")), 11),
        (Tree.Path(List("", "bar", "b")), 12)
      )
      assertEquals(result, expected)
    }
  }

  test("Flatten tree with a single leaf") {
    val singleLeafTree = Tree[String, Int]("").updated("/ass/a".toPath, 1)
    val result = singleLeafTree.flatten
    val expected = List((Tree.Path(List("", "ass", "a")), 1))
    assertEquals(result, expected)
  }

  test("Flatten empty tree") {
    val emptyTree = Tree[String, Int]("")
    val result = emptyTree.flatten
    val expected = List.empty[(Tree.Path[String], Int)]
    assertEquals(result, expected)
  } // D O N E

  test("Add new empty internal node") {
    new TestTree {
      val newPath = Tree.Path(List("", "foo", "b", "c", "d"))
      val result = t.updated(newPath).toString
      val expected = "Node(,List(Node(foo,List(Leaf(a,1), Node(b,List(Node(c,List(Node(d,List()))))))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))"
      assertEquals(result, expected)
    }
  }

  test("Replace existing leaf with internal node") {
    new TestTree {
      val newPath = Tree.Path(List("", "bar"))
      val result = t.updated(newPath).toString
      val expected = "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Node(bar,List())))"
      assertEquals(result, expected)
    }
  }

  test("Replace existing node") {
    new TestTree {
      val newPath = Tree.Path(List("", "foo"))
      val result = t.updated(newPath).toString
      val expected = "Node(,List(Node(foo,List()), Node(bar,List(Leaf(a,11), Leaf(b,12)))))"
      assertEquals(result, expected)
    }
  }

  test("Replace existing leaf with internal node (discarding payload)") {
    new TestTree {
      val newPath = Tree.Path(List("", "bar"))
      val payload = 42069
      val result = t.updated(newPath, payload).toString
      val expected = "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Leaf(bar,42069)))"
      assertEquals(result, expected)
    }
  }

  test("Replace existing node with leaf carrying payload") {
    new TestTree {
      val newPath = Tree.Path(List("", "foo"))
      val payload = 42069
      val result = t.updated(newPath, payload).toString
      val expected = "Node(,List(Leaf(foo,42069), Node(bar,List(Leaf(a,11), Leaf(b,12)))))"
      assertEquals(result, expected)
    }
  }

  test("Add node with leaf carrying payload") {
    new TestTree {
      val newPath = Tree.Path(List("", "ass"))
      val payload = 455
      val result = t.updated(newPath, payload).toString
      val expected = "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Node(bar,List(Leaf(a,11), Leaf(b,12))), Leaf(ass,455)))"
      assertEquals(result, expected)
    }
  }

  test("Construct tree with empty internal node as root") {
    val key = "root"
    val tree = Tree(key)
    val expected = s"Node($key,List())"
    assertEquals(tree.toString, expected)
  } // D O N E

  test("Construct tree with empty internal node at path") {
    val path = Tree.Path(List("cheng", "siu", "wing", "ass"))
    val tree = Tree(path)
    val expected = "Node(cheng,List(Node(siu,List(Node(wing,List(Node(ass,List())))))))"
    assertEquals(tree.toString, expected)
  } // MISSING CONSTRUCTOR

  test("Construct tree with single leaf at path with payload") {
    val path = "/lig/ma".toPath
    val payload = 84115
    val tree = Tree(path, payload)
    val expected = "Node(,List(Node(lig,List(Leaf(ma,84115)))))"
    assertEquals(tree.toString, expected)
  } // MISSING CONSTRUCTOR

  test("Construct root") {
    val path = Tree.Path(List(""))
    val tree = Tree(path)
    val expected = "Node(,List())"
    assertEquals(tree.toString, expected)
  } // D O N E

  test("Construct a tree with repeated keys") {
    val tree = Tree("15")
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
      .updated("15/10/2023".toPath)
    val expected = "Node(15,List(Node(10,List(Node(2023,List())))))"
    assertEquals(tree.toString, expected)
  } // D O N E

  test("Botha") {
    assert(plsfail(() => {
      val path = Tree("deez")
        .updated("deez/nuts".toPath)
        .updated("deez_/nuts".toPath)
    }))
  } // D O N E

  test("Path is empty or does not start at the root (IllegalPathException)") {
    new TestTree {
      val emptyPath = Tree.Path(List("").tail)
      assert(plsfail(() => {
        t.updated(emptyPath, 42)
      }))
    }
  } // D O N E

  test("Path is empty or does not start at the root (IllegalPathException) 2") {
    new TestTree {    
      val invalidPath = Tree.Path(List("a", "b"))
      assert(plsfail(() => {
        t.updated(invalidPath, 42)
      }))
    }
  } // D O N E

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
