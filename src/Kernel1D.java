public class Kernel1D extends Kernel {
    private double[] kernel;
    private final double C = 1.0/12.0;

    // todo for now the kernel is predefined - create method e.g. for a Gaussian kernel
    // (1/12)[-1 8 0 -8 1]
    Kernel1D() {
        kernel[0] = C*(-1.0);
        kernel[1] = C*(8.0);
        kernel[2] = 0;
        kernel[3] = -1.0*kernel[1];
        kernel[4] = -1.0*kernel[0];
    }

    public double[] get() {
        return kernel;
    }
}
