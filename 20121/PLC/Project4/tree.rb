# Christopher Wood

class Leaf 
  include Enumerable

  # DEFINE initialize HERE
  def initialize
  end

  # DEFINE size HERE
  def size
    return 0
  end

  # DEFINE height HERE
  def height
    return 0
  end

  # DEFINE deepest HERE
  def deepest
    nil
  end

  # DEFINE to_s HERE
  def to_s
    return nil.to_s
  end

  # As per assignment requirements
  include Enumerable

  # DEFINE each HERE
  def each 
    self
  end

  # DEFINE tmap HERE
  def tmap
    Leaf.new
  end

end

class BinaryNode 
  include Enumerable

  # DEFINE initialize HERE
  def initialize(element, leftChild, rightChild)
    @element = element
    @leftChild = leftChild
    @rightChild = rightChild
  end

  # DEFINE size HERE
  def size
    return 1 + @leftChild.size + @rightChild.size
  end

  # DEFINE height HERE
  def height
    return [@leftChild.height + 1, @rightChild.height + 1].max
  end

  # DEFINE deepest HERE
  def deepest
    if height == 0
      return nil
    elsif (height == 1)
      return @element
    else 
      leftHeight = @leftChild.height
      rightHeight = @rightChild.height

      if (leftHeight == rightHeight)
        return [@leftChild.deepest, @rightChild.deepest]
      elsif (leftHeight > rightHeight)
        return @leftChild.deepest
      else 
        return @rightChild.deepest
      end
    end
  end

  # DEFINE to_s HERE
  def to_s
    return @element.to_s + @leftChild.to_s + @rightChild.to_s
  end

  # As per assignment requirements
  include Enumerable

  # DEFINE each HERE
  def each &block
    yield @element
    @leftChild.each &block
    @rightChild.each &block
    return self
  end

  # DEFINE tmap HERE
  def tmap &block
    element = @element
    leftChild = @leftChild.clone
    rightChild = @rightChild.clone
    if block_given?
      element = block.call(element)
      leftChild = leftChild.tmap &block
      rightChild = rightChild.tmap &block
    end
    BinaryNode.new(element, leftChild, rightChild)
  end

end

class NaryNode
  include Enumerable

  # DEFINE initialize HERE
  def initialize(element, children)
    @element = element
    @children = children.clone
  end

  # DEFINE size HERE
  def size
    sum = 1
    @children.each {|x| sum = sum + x.size}
    return sum
  end

  # DEFINE height HERE
  def height
    max = 0
    @children.each {|c| 
      childHeight = c.height
      max = childHeight if childHeight >= max
    }
    return max + 1
  end

  # DEFINE deepest HERE
  def deepest
    if height == 0
      return nil
    elsif height == 1
      return @element
    else 
      max = 0
      deepOnes = []

      # Build up the deep ones array
      @children.each {|x| 
        if (x.height > max)
          max = x.height
          deepOnes = [x.deepest]
        elsif (x.height == max)
          deepOnes << x.deepest
        end
      }

      if (deepOnes.length == 1)
        return deepOnes[0]
      else
        return deepOnes
      end
    end
  end

  # DEFINE to_s HERE
  def to_s
    result = @element.to_s
    @children.each {|c| result = result + c.to_s}
    return result
  end

  # As per assignment requirements
  include Enumerable

  # DEFINE each HERE
  def each &block
    yield @element
    @children.each {|c| c.each &block}
    return self
  end

  # DEFINE tmap HERE
  def tmap &block
    element = @element
    children = @children.clone
    if block_given?
      element = block.call(element)
      children = []
      @children.each {|c| children << (c.tmap &block)}
    end
    NaryNode.new(element, children)
  end

end

# DEFINE tree_to_s HERE
def tree_to_s t
  result = ""
  t.each {|c| result = result + c.to_s}
  return result
end
