import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
class NextItemReturnTypeDifferentFromIteratorAnnotationValue {

    @Iterator.CurrentItem
    Integer current() { return 0; }

    @Iterator.IsDone
    boolean isDone() { return true; }

    @Iterator.NextItem
    Float next() { return 0f; }
}
