package utils;

public final class Tuple<F, S> {
    public final F fst;
    public final S snd;
    
    private Tuple(F fst, S snd) {
        this.fst = fst;
        this.snd = snd;
    }
    
    public static <F, S> Tuple<F, S> of(F fst, S snd) {
        return new Tuple<>(fst, snd);        
    }
    
    @Override
    public String toString() {
        return "('" + fst + "', '" + snd + "')";
    }     
}
