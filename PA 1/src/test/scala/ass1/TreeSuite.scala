package ass1

/**
 * This class is a test suite for the methods in object Tree.
 *
 * To run this test suite, start "sbt" then run the "test" command.
 */
class TreeSuite extends munit.FunSuite:
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

  test("Sample tree") {
    new TestTree:
      assertEquals(t.toString, "Node(,List(Node(foo,List(Leaf(a,1), Leaf(b,2))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))")
  }

  test("Tree.get") {
    new TestTree:
      assertEquals(t.get("".toPath).toString, "Some(Left(List(foo, bar)))")
  }

  test("Tree.updated") {
    new TestTree:
      assertEquals(t.updated("/foo/b/c/d".toPath, 42).toString, "Node(,List(Node(foo,List(Leaf(a,1), Node(b,List(Node(c,List(Leaf(d,42))))))), Node(bar,List(Leaf(a,11), Leaf(b,12)))))")
  }

  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
