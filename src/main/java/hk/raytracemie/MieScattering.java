package hk.raytracemie;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.special.BesselJ;

public class MieScattering {
    private double radius;
    private double wavelength;
    private Complex refractiveIndex;
    private final double H = 1e-14;
    private double sizeParameter = 0;

    public MieScattering(double radius, double wavelength, double refractiveIndexReal, double refractiveIndexImag) {
        // Adding constraints to radius and wavelength
        this.radius = Math.max(1e-9, radius); // Avoid too small radius
        this.wavelength = Math.max(1e-9, wavelength); // Avoid too small wavelength
        this.refractiveIndex = new Complex(refractiveIndexReal, refractiveIndexImag);
        this.sizeParameter = 2 * Math.PI * this.radius / this.wavelength;
    }

    public double getSizeParameter() {
        return this.sizeParameter;
    }

    private double sphericalBesselJ(int n, double x) {
        if (x == 0) return 0;
        return Math.sqrt(Math.PI / (2 * x)) * new BesselJ(n + 0.5).value(x);
    }

    private double sphericalBesselJPrime(int n, double x) {
        return (sphericalBesselJ(n, x + H) - sphericalBesselJ(n, x - H)) / (2 * H);
    }

    private Complex sphericalHankel(int n, double x) {
        if (x == 0) return Complex.ZERO;
        double jn = sphericalBesselJ(n, x);
        double yn = -Math.cos(x) / Math.sqrt(x);
        return new Complex(jn, yn);
    }

    private Complex sphericalHankelPrime(int n, double x) {
        Complex hankelPlus = sphericalHankel(n, x + H);
        Complex hankelMinus = sphericalHankel(n, x - H);
        return hankelPlus.subtract(hankelMinus).divide(2 * H);
    }

    private Complex[] calculateMieCoefficients(int n) {
        double x = getSizeParameter();
        if (x == 0) return new Complex[]{Complex.ZERO, Complex.ZERO};
        Complex m = refractiveIndex;

        double psi_n_x = sphericalBesselJ(n, x);
        double psi_n_x_prime = sphericalBesselJPrime(n, x);
        Complex psi_n_mx = new Complex(sphericalBesselJ(n, m.getReal() * x), 0);
        Complex psi_n_mx_prime = new Complex(sphericalBesselJPrime(n, m.getReal() * x), 0);
        Complex xi_n_x = sphericalHankel(n, x);
        Complex xi_n_x_prime = sphericalHankelPrime(n, x);

        if (xi_n_x_prime.equals(Complex.ZERO)) return new Complex[]{Complex.ZERO, Complex.ZERO};

        Complex numeratorA = m.multiply(psi_n_mx).multiply(psi_n_x_prime).subtract(new Complex(psi_n_x, 0).multiply(psi_n_mx_prime));
        Complex denominatorA = m.multiply(psi_n_mx).multiply(xi_n_x_prime).subtract(xi_n_x.multiply(psi_n_mx_prime));
        Complex a_n = numeratorA.divide(denominatorA);

        Complex numeratorB = psi_n_mx.multiply(psi_n_x_prime).subtract(m.multiply(new Complex(psi_n_x, 0)).multiply(psi_n_mx_prime));
        Complex denominatorB = psi_n_mx.multiply(xi_n_x_prime).subtract(m.multiply(xi_n_x).multiply(psi_n_mx_prime));
        Complex b_n = numeratorB.divide(denominatorB);

        return new Complex[]{a_n, b_n};
    }

    public double calculateScatteringEfficiency() {
        double x = getSizeParameter();
        if (x > 1e3) {
            System.err.println("Warning: Size parameter x is very large, results may not be accurate.");
            return 0; // Return 0 or some default value to avoid computation
        }
        double Q_sca = 0;
        for (int n = 1; n <= 100; n++) {
            Complex[] coefficients = calculateMieCoefficients(n);
            double anAbs = coefficients[0].abs();
            double bnAbs = coefficients[1].abs();
            Q_sca += (2 * n + 1) * (anAbs * anAbs + bnAbs * bnAbs);
        }
        double scatteringEfficiency = (2 / (x * x)) * Q_sca;
        return scatteringEfficiency;
    }

    public double calculateAbsorptionEfficiency() {
        double x = getSizeParameter();
        if (x > 1e3) {
            System.err.println("Warning: Size parameter x is very large, results may not be accurate.");
            return 0; // Return 0 or some default value to avoid computation
        }
        double Q_abs = 0;
        for (int n = 1; n <= 100; n++) {
            Complex[] coefficients = calculateMieCoefficients(n);
            double anReal = coefficients[0].getReal();
            double bnReal = coefficients[1].getReal();
            double anAbs = coefficients[0].abs();
            double bnAbs = coefficients[1].abs();
            Q_abs += (2 * n + 1) * (anReal + bnReal - anAbs * anAbs - bnAbs * bnAbs);
        }
        double absorptionEfficiency = (2 / (x * x)) * Q_abs;
        return absorptionEfficiency;
    }

    public double calculatePhaseFunction(double cosTheta) {
        double g = 0.9;
        double phaseFunction = (1 - g * g) / Math.pow(1 + g * g - 2 * g * cosTheta, 1.5);
        return phaseFunction;
    }

    public Complex getRefractiveIndex() {
        return refractiveIndex;
      }

}
