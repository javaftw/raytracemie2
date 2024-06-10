package hk.raytracemie;

public class Color {
    public double r, g, b;

    // Constructor with double values (0-1 range)
    public Color(double r, double g, double b) {
        this.r = clamp(r, 0.0, 1.0);
        this.g = clamp(g, 0.0, 1.0);
        this.b = clamp(b, 0.0, 1.0);
    }

    // Constructor with int hex value (e.g., 0xff00ff)
    public Color(int hex) {
        this.r = ((hex >> 16) & 0xFF) / 255.0;
        this.g = ((hex >> 8) & 0xFF) / 255.0;
        this.b = (hex & 0xFF) / 255.0;
    }

    // Constructor with int values (0-255 range)
    public Color(int r, int g, int b) {
        this.r = clamp(r / 255.0, 0.0, 1.0);
        this.g = clamp(g / 255.0, 0.0, 1.0);
        this.b = clamp(b / 255.0, 0.0, 1.0);
    }

    // Convert to 0-255 range and cap values
    public int toInt() {
        int ir = (int) (255.99 * clamp(r, 0.0, 1.0));
        int ig = (int) (255.99 * clamp(g, 0.0, 1.0));
        int ib = (int) (255.99 * clamp(b, 0.0, 1.0));
        return (ir << 16) | (ig << 8) | ib;
    }

    // Clamp function to keep values within a specific range
    private double clamp(double x, double min, double max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    // Normalize color values (0-1 range)
    public Color normalize() {
        double max = Math.max(r, Math.max(g, b));
        if (max > 1.0) {
            return new Color(r / max, g / max, b / max);
        }
        return this;
    }

    // Multiply by a scalar
    public Color multiply(double scalar) {
        return new Color(r * scalar, g * scalar, b * scalar);
    }

    // Multiply by another color
    public void multiply(Color other) {
        this.r = clamp(this.r * other.r, 0.0, 1.0);
        this.g = clamp(this.g * other.g, 0.0, 1.0);
        this.b = clamp(this.b * other.b, 0.0, 1.0);
    }

    // Add two colors
    public void add(Color other) {
        this.r = clamp(this.r + other.r, 0.0, 1.0);
        this.g = clamp(this.g + other.g, 0.0, 1.0);
        this.b = clamp(this.b + other.b, 0.0, 1.0);
    }
    
    public String toString() {
        return this.r + "," + this.g + "," + this.b;
    }
    
}
