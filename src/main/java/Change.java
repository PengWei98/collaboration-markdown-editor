public abstract class Change  implements java.io.Serializable{
    int retain;
}

class Insert extends Change{
    String insert;
    Insert(int retain, String insert){
        this.retain = retain;
        this.insert = insert;
    }
}


class Delete extends Change{
//    String delete;
    Delete(int retain){
        this.retain = retain;
//        this.delete = delete;
    }
}

