import javax.swing.*;
        import java.awt.*;
        import java.awt.event.*;
        import java.awt.image.BufferedImage;

public class MandelbrotViewer extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final int MAX_ITER = 1000;
    private double zoom = 150;
    private double offsetX = -3.0, offsetY = -1.5; // Initial offsets to center the Mandelbrot set
    private BufferedImage fractalImage;
    private int dragStartX, dragStartY;
    private boolean needRedraw = true;

    public MandelbrotViewer() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setPreferredSize(new Dimension(800, 600));
    }

    private void generateFractal() {
        if (!needRedraw) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        fractalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double a = (x - width / 2.0) / zoom + offsetX;
                double b = (y - height / 2.0) / zoom + offsetY;
                Complex c = new Complex(a, b);
                Complex z = new Complex(0, 0);
                int iter = 0;
                while (iter < MAX_ITER && z.abs() <= 2.0) {
                    z = z.square().add(c);
                    iter++;
                }
                float hue = 0.7f + (float) iter / MAX_ITER;
                int color = iter >= MAX_ITER ? 0 : Color.HSBtoRGB(hue, 1f, 1f);
                fractalImage.setRGB(x, y, color);
            }
        }
        needRedraw = false;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (needRedraw) {
            generateFractal();
        }
        g.drawImage(fractalImage, 0, 0, this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double scaleFactor = Math.pow(1.05, -e.getWheelRotation());
        double newZoom = zoom * scaleFactor;

        double mouseRelX = e.getX() - getWidth() / 2.0;
        double mouseRelY = e.getY() - getHeight() / 2.0;

        offsetX += (mouseRelX / zoom) - (mouseRelX / newZoom);
        offsetY += (mouseRelY / zoom) - (mouseRelY / newZoom);

        zoom = newZoom;
        needRedraw = true;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragStartX = e.getX();
        dragStartY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - dragStartX;
        int dy = e.getY() - dragStartY;
        offsetX -= dx / zoom;
        offsetY -= dy / zoom;
        dragStartX = e.getX();
        dragStartY = e.getY();
        needRedraw = true;
        repaint();
    }

    // Unused mouse listener methods
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    private static class Complex {
        private final double re;
        private final double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex square() {
            return new Complex(re * re - im * im, 2 * re * im);
        }

        public Complex add(Complex other) {
            return new Complex(re + other.re, im + other.im);
        }

        public double abs() {
            return Math.sqrt(re * re + im * im);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mandelbrot Set Viewer");
            MandelbrotViewer viewer = new MandelbrotViewer();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(viewer);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
