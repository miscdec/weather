package com.opweather.gles20.util;

public class Geometry {

    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(this.center, this.radius * scale);
        }
    }

    public static class Cylinder {
        public final Point center;
        public final float height;
        public final float radius;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class Plane {
        public final Vector normal;
        public final Point point;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static class Point {
        public final float x;
        public final float y;
        public final float z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance) {
            return new Point(this.x, this.y + distance, this.z);
        }

        public Point translate(Vector vector) {
            return new Point(this.x + vector.x, this.y + vector.y, this.z + vector.z);
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static class Vector {
        public final float x;
        public final float y;
        public final float z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float) Math.sqrt((double) (((this.x * this.x) + (this.y * this.y)) + (this.z * this.z)));
        }

        public Vector crossProduct(Vector other) {
            return new Vector((this.y * other.z) - (this.z * other.y), (this.z * other.x) - (this.x * other.z), (this
                    .x * other.y) - (this.y * other.x));
        }

        public float dotProduct(Vector other) {
            return ((this.x * other.x) + (this.y * other.y)) + (this.z * other.z);
        }

        public Vector scale(float f) {
            return new Vector(this.x * f, this.y * f, this.z * f);
        }

        public Vector normalize() {
            return scale(1.0f / length());
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    public static float distanceBetween(Point point, Ray ray) {
        return vectorBetween(ray.point, point).crossProduct(vectorBetween(ray.point.translate(ray.vector), point))
                .length() / ray.vector.length();
    }

    public static Point intersectionPoint(Ray ray, Plane plane) {
        return ray.point.translate(ray.vector.scale(vectorBetween(ray.point, plane.point).dotProduct(plane.normal) /
                ray.vector.dotProduct(plane.normal)));
    }
}
