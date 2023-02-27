package jrb.Builder;


class EitherOf extends Builder{
    
    @Override
    protected String getGroup() {
        return "(?:%s)";
    }

    @Override
    protected String getImplodeString() {
        return "|";
    }
}
