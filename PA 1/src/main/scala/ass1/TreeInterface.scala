package ass1

/**
 * The interface of the trees. You should not edit any code here.
 */
trait TreeInterface[K, V]:
  import Tree.Path
  def key: K
  def contains(path: Path[K]): Boolean
  def get(path: Path[K]): Option[Either[List[K], V]]
  def flatten: List[(Path[K], V)]
  def updated(path: Path[K]): Tree[K, V]
  def updated(path: Path[K], payload: V): Tree[K, V]

trait TreeComp:
  def apply[K, V](key: K): Tree[K, V]
  def apply[K, V](path: Path[K]): Tree[K, V]
  def apply[K, V](path: Path[K], payload: V): Tree[K, V]
  case class Path[K](segs: List[K])
  object IllegalPathException extends java.lang.Exception
