package ass1

import scala.collection.View.Empty
import javax.lang.model.`type`.NullType

enum Tree[K, V] extends TreeInterface[K, V]:
  private case Leaf(key: K, payload: V)
  private case Node(key: K, children: List[Tree[K, V]])

  import Tree.Path

  def contains(path: Path[K]): Boolean = 
    if (path.segs.isEmpty) then false
    else this match
    case Node(key, children) =>
      path.segs match
        case x::Nil => x==key
        case xs => 
          if key==xs.head then children.find(xs.tail.head==_.key) match
            case Some(x) => x.contains(Path(xs.tail))
            case None => false
          else false
    case Leaf(key, payload) =>
      path.segs match
        case x::Nil => x==key
        case _ => false

  def get(path: Path[K]): Option[Either[List[K], V]] = 
    if (path.segs.isEmpty) then None
    else this match
    case Node(key, children) => path.segs match
      case x::Nil => if (x==key) then Some(Left(children.map(_.key))) else None
      case xs => children.find(xs.tail.head==_.key) match
                    case Some(x) =>  x.get(Path(xs.tail))
                    case None => None
    case Leaf(key, payload) => path.segs match
      case x::Nil => if (x==key) then Some(Right(payload)) else None
      case xs => None

  def flatten: List[(Path[K], V)] = 
    def recur(node: Tree[K, V], path:List[K]): List[(Path[K], V)] = node match
      case Node(key, children) => children.flatMap(recur(_, path:+key))
      case Leaf(key, payload) => List((Path(path:+key), payload))
    recur(this, Nil)

  def updated(path: Path[K]): Tree[K, V] = 
    if (path.segs.isEmpty) then throw Tree.IllegalPathException
    else if (this.key != path.segs.head) then throw Tree.IllegalPathException 
    else 
      this match
        case Node(key, children) => path.segs match
          case x::Nil => Node(x, Nil)
          case xs => 
            children.find(_.key==xs.tail.head) match
              case None => Node(key, children.dropWhile(xs.tail.head==_.key):+Node(xs.tail.head, Nil).updated(Path(xs.tail)))
              case Some(x) => Node(key, children.map(child => if child==x then x.updated(Path(xs.tail)) else child))
        case Leaf(key, payload2) => path.segs match
          case x::Nil => Node(key, Nil)
          case x::y::Nil => Node(key, List(Node(y, Nil)))
          case xs => Node(key, List(Node(xs.tail.head, Nil).updated(Path(xs.tail))))

  def updated(path: Path[K], payload: V): Tree[K, V] = 
    if (path.segs.isEmpty) then throw Tree.IllegalPathException
    else if (this.key != path.segs.head) then throw Tree.IllegalPathException
    else 
      this match
        case Node(key, children) => path.segs match
          case x::Nil => Leaf(key, payload)
          case xs => 
            children.find(_.key==xs.tail.head) match
              case None => Node(key, children.dropWhile(xs.tail.head==_.key):+Node(xs.tail.head, Nil).updated(Path(xs.tail), payload))
              case Some(x) => Node(key, children.map(child => if child==x then x.updated(Path(xs.tail), payload) else child))

        case Leaf(key, payload2) => path.segs match
          case x::Nil => Leaf(key, payload)
          case x::y::Nil => Node(y, List(Leaf(y, payload)))
          case xs => Node(key, List(Node(xs.tail.head, Nil).updated(Path(xs.tail), payload)))              

object Tree extends TreeComp:
  def apply[K, V](key: K): Tree[K, V] = Node(key, Nil)

  def apply[K, V](path: Path[K]): Tree[K, V] = 
    if path.segs==Nil then throw Tree.IllegalPathException
    Node(path.segs.head, Nil).updated(path)

  def apply[K, V](path: Path[K], payload: V): Tree[K, V] = 
    if path.segs==Nil then throw Tree.IllegalPathException
    Node(path.segs.head, Nil).updated(path, payload)
