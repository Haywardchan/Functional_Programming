package ass1

import scala.collection.View.Empty

enum Tree[K, V] extends TreeInterface[K, V]:
  private case Leaf(key: K, payload: V)
  private case Node(key: K, children: List[Tree[K, V]])

  import Tree.Path

  def contains(path: Path[K]): Boolean = {
    def containsHelper(currTree: Tree[K, V], currPath: List[K]): Boolean = (currTree, currPath) match {
      case (_, Nil) => true // Empty path, so the tree itself is considered to contain it
      case (Leaf(key, _), seg :: Nil) => key == seg // Check if the leaf node contains the segment
      case (Node(key, children), seg :: remaining) =>
        if (key == seg) {
          containsHelper(Node(key, children), remaining)
        } else {
          false
        }
      case _ => false // Invalid path or mismatched tree type
    }
    containsHelper(this, path.segs)
  }

  def get(path: Path[K]): Option[Either[List[K], V]] = 
    if !contains(path) then None
    else {
      var currnode = this //root node
      for (key <- path.segs) {
        currnode match {
          case Node(_, children) => currnode = children.find(_.key==key)
          case _ => None
        }
      }
      currnode match {
        case Node(key, child) => 
          val ls = child.map(_.key)
          Some(Left(ls))
        case Leaf(_, payload) => Some(Right(payload))
      }
    }

  def flatten: List[(Path[K], V)] = ???
  //   this match {
  //   case Nil => Nil
  //   case singlepath => singlepath
  //   case _ => flatten(Left(this)) ++ flatten(Right(this))
  // }


  def updated(path: Path[K]): Tree[K, V] = ???

  def updated(path: Path[K], payload: V): Tree[K, V] = ???

object Tree extends TreeComp:
  def apply[K, V](key: K): Tree[K, V] = Node(key, Nil)

  def apply[K, V](path: Path[K]): Tree[K, V] = path match
    case Path(Nil) => throw IllegalPathException
    case _ => 
      def listToNode[K](list: List[K]): Tree[K, V] = {
        list.foldRight[Tree[K, V]](Node(list.last, Nil)) {
          case (item, acc) => Node(item, List(acc))
        }
      }
      listToNode(path.segs)

  def apply[K, V](path: Path[K], payload: V): Tree[K, V] = path match
    case Path(Nil) => throw IllegalPathException
    case _ => 
      def listToNode[K](list: List[K]): Tree[K, V] = {
        list.foldRight[Tree[K, V]](Leaf(list.last, payload)) {
          case (item, acc) => Node(item, List(acc))
        }
      }
      listToNode(path.segs)
