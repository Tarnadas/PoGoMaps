package com.pokemongomap.searcher;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Cell {

    private static final long IN_USE_THRESHOLD = 180000;

    private List<Integer> mPath;
    private LatLng mSource, mLocation;
    private Date timeStamp;
    private boolean mInRange = true;
    public Cell(int[] ints) {
        mPath = new LinkedList<>();
        for (int i = 0; i < ints.length; i++) {
            mPath.add(ints[i]);
        }
        Iterator<Integer> it = mPath.iterator();
        while (it.hasNext()) {
            int i = it.next();
            if (i == -1) {
                it.remove();
            }
        }
    }
    public Cell(Cell copyCell) {
        mPath = new LinkedList<>();
        for (int i : copyCell.getPath()) {
            mPath.add(i);
        }
    }
    public List<Integer> getPath() {
        return mPath;
    }
    public LatLng getSource() {
        return mSource;
    }
    public LatLng getLocation() {
        return mLocation;
    }
    public void setSource(LatLng source) {
        mSource = source;
    }
    public void setLocation(LatLng target) {
        mLocation = target;
    }
    public boolean isInUse() {
        if (timeStamp == null) return false;
        Date current = new Date();
        if (current.getTime() - timeStamp.getTime() < IN_USE_THRESHOLD) {
            return true;
        } else {
            timeStamp = null;
            return false;
        }
    }
    public void use() {
        timeStamp = new Date();
    }
    public void reuse() {
        timeStamp = null;
    }
    public boolean isInRange() {
        return mInRange;
    }
    public void setInRange(boolean inRange) {
        mInRange = inRange;
    }
    public Cell getAdjacentCell(int direction, double displacement, List<Cell> cells) {
        return getRealClosest(getDisplacementEstimate(this, displacement, direction * Math.PI/3), cells);
    }
    public void recreatePath(Cell cell, double displacement, List<Cell> oldCells) {

        mSource = cell.getLocation();
        List<Integer> newPath = new LinkedList<>();
        Cell currentCell = cell;

        int a = -1, b = -1;
        while (!currentCell.equals(this)) {
            int direction;
            double arc = Math.atan2(getLocation().latitude - currentCell.getLocation().latitude,
                    getLocation().longitude - currentCell.getLocation().longitude);
            if (arc >= -Math.PI/6 && arc <= Math.PI/6) {
                direction = 0;
            } else if (arc >= Math.PI/6 && arc <= Math.PI*1/2) {
                direction = 1;
            } else if (arc >= Math.PI*1/2 && arc <= Math.PI*5/6) {
                direction = 2;
            } else if (arc >= Math.PI*5/6 && arc <= Math.PI*7/6) {
                direction = 3;
            } else if (arc >= Math.PI*7/6 && arc <= Math.PI*3/2) {
                direction = 4;
            } else {
                direction = 5;
            }
            if (a == -1) {
                a = direction;
                b = direction;
            } else if (a != direction) {
                b = direction;
            }
            if (Math.abs(a-b) > 1 && !((a == 0 && b == 5) || (a == 5 && b == 0))) break;
            if (a != direction && b != direction)  break;
            newPath.add(direction);
            if (newPath.size() == mPath.size() + cell.getPath().size()) break;
            Cell nextCell = currentCell.getAdjacentCell(direction, displacement, oldCells);
            if (nextCell == null || nextCell.equals(currentCell)) {
                break;
            }
            currentCell = nextCell;
        }
        List<Integer> orderedPath = new LinkedList<>();
        int hi = 0;
        int lo = 5;
        if (a < lo) lo = a;
        if (b < lo) lo = b;
        if (a > hi) hi = a;
        if (b > hi) hi = b;
        for (int i : newPath) {
            if (i < lo) lo = i;
        }
        for (int i : newPath) {
            if (i > hi) hi = i;
        }
        for (int i : newPath) {
            if (lo == 0 && hi == 5) {
                if (i == hi) {
                    orderedPath.add(0, i);
                } else {
                    orderedPath.add(i);
                }
            } else {
                if (i == hi) {
                    orderedPath.add(i);
                } else {
                    orderedPath.add(0, i);
                }
            }
        }

        if (orderedPath.size() == 0) {
            mLocation = cell.getLocation();
        }
        mPath = orderedPath;
    }
    public boolean isPreceding(Cell cell) {
        if (this.equals(cell) || getPath().size() >= cell.getPath().size()) {
            return false;
        }
        int count = cell.getPathCount() - cell.getPath().get(cell.getPath().size()-1);
        if (count != getPathCount()) {
            return false;
        }
        return cell.getPath().size() == mPath.size()+1;
    }
    public Cell getPreceding(List<Cell> cells) {
        if (cells == null || getPath().size() == 0) return null;
        for (Cell cell : cells) {
            if (cell.isPreceding(this)) {
                return cell;
            }
        }
        return null;
    }
    public int getPathCount() {
        int count = 0;
        for (int i : mPath) {
            count += i;
        }
        return count;
    }
    public boolean hasPath(Object obj) {
        if (!(obj instanceof  Cell)) return false;
        Cell cell = (Cell) obj;
        int pathCount = cell.getPathCount();
        if (getPathCount() == pathCount && getPath().size() == cell.getPath().size()) {
            return true;
        }
        return false;
    }
    @Override
    public boolean equals (Object obj) {
        if (!(obj instanceof  Cell)) return false;
        Cell cell = (Cell) obj;
        return cell.getLocation() != null && mLocation != null && Math.abs(cell.getLocation().latitude - mLocation.latitude) < 0.00000000000000001d &&
                Math.abs(cell.getLocation().longitude - mLocation.longitude) < 0.000000000000000001d;
    }
    @Override
    public int hashCode() {
        int result = 0;
        for (int i : mPath) {
            result = (result << 3) | i;
        }
        return result;
    }

    public static Cell[] createCells(int radius) {
        int size = 1;
        for (int i = 0; i < radius;) {
            i++;
            size += i * 6;
        }
        Cell[] result = new Cell[size];
        int a = -1, b = 0, c = 0;
        for (int i = 0; i < result.length; i++) {
            if (++a > b) {
                a = 1;
                b += 6;
                c++;
            }
            int[] bits;
            if (b == 0){
                bits = new int[1];
            } else {
                bits = new int[b];
            }
            int d = 1;
            for (int j = 0,k = 0; j < bits.length; j++) {
                bits[j] = k;
                if (d == c) {
                    d = 0;
                    k++;
                }
                d++;
            }

            int[] push = new int[radius+1];
            for (int j = 0; j < push.length; j++) {
                push[j] = -1;
            }
            for (int j = 0; j < c; j++) {
                int index = i-1+j;
                for (int k = 0; k < c-1; k++) {
                    index -= (k+1) * 6;
                }
                if (index >= bits.length) index -= bits.length;
                push[j] = bits[index];
            }
            result[i] = new Cell(push);

        }
        return result;
    }

    private static LatLng getDisplacementEstimate(Cell cell, double displacement, double alpha) {

        //Earth’s radius, sphere
        double earthRadius = 6378137;

        //offsets in meters
        double dLatM = Math.sin(alpha) * displacement;
        double dLonM = Math.cos(alpha) * displacement;

        //Coordinate offsets in radians
        double dLat = dLatM / earthRadius;
        double dLon = dLonM / (earthRadius * Math.cos(Math.PI * cell.getLocation().latitude / 180));

        //OffsetPosition, decimal degrees
        double newLat = cell.getLocation().latitude + dLat * 180 / Math.PI;
        double newLon = cell.getLocation().longitude + dLon * 180 / Math.PI;

        return new LatLng(newLat, newLon);
    }

    public static LatLng getDisplacementEstimate(LatLng loc, double displacement, double alpha) {

        //Earth’s radius, sphere
        double earthRadius = 6378137;

        //offsets in meters
        double dLatM = Math.sin(alpha) * displacement;
        double dLonM = Math.cos(alpha) * displacement;

        //Coordinate offsets in radians
        double dLat = dLatM / earthRadius;
        double dLon = dLonM / (earthRadius * Math.cos(Math.PI * loc.latitude / 180));

        //OffsetPosition, decimal degrees
        double newLat = loc.latitude + dLat * 180 / Math.PI;
        double newLon = loc.longitude + dLon * 180 / Math.PI;

        return new LatLng(newLat, newLon);
    }

    public void setDisplacementEstimate(Cell sourceCell, double displacement, List<Cell> cells) {

        //Earth’s radius, sphere
        double earthRadius = 6378137;

        Cell preceding = getPreceding(cells);
        LatLng source;
        if (preceding == null) {
            setSource(sourceCell.getSource());
            setLocation(sourceCell.getSource());
            return;
        }
        source = preceding.getLocation();

        double alpha = getPath().get(getPath().size()-1) * Math.PI / 3;

        //offsets in meters
        double dLatM = Math.sin(alpha) * displacement;
        double dLonM = Math.cos(alpha) * displacement;

        //Coordinate offsets in radians
        double dLat = dLatM / earthRadius;
        double dLon = dLonM / (earthRadius * Math.cos(Math.PI * source.latitude / 180));

        //OffsetPosition, decimal degrees
        double newLat = source.latitude + dLat * 180 / Math.PI;
        double newLon = source.longitude + dLon * 180 / Math.PI;

        setSource(sourceCell.getSource());
        setLocation(new LatLng(newLat, newLon));
    }

    public static boolean isCellInRange(Cell cell, LatLng loc, int radius, double displacement) {
        boolean inRange = distanceInMeter(cell.getLocation(), loc) < (radius+1) * displacement;
        cell.setInRange(inRange);
        return inRange;
    }

    private static double distance(LatLng loc0, LatLng loc1) {
        double dLat = loc0.latitude - loc1.latitude;
        double dLon = loc0.longitude - loc1.longitude;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }

    public static double distanceInMeter(LatLng loc1, LatLng loc2) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = (loc2.latitude - loc1.latitude) * Math.PI / 180;
        double dLon = (loc2.longitude - loc1.longitude) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(loc1.latitude * Math.PI / 180) * Math.cos(loc2.latitude * Math.PI / 180) *
                Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    private static Cell getRealClosest(LatLng loc, List<Cell> cells) {
        for (Cell cell : cells) {
            LatLng target = cell.getLocation();
            if (Math.abs(target.latitude-loc.latitude) < 0.000000001 && Math.abs(target.longitude-loc.longitude) < 0.000000001) {
                return cell;
            }
        }
        return null;
    }

    public static Cell getClosest(LatLng loc, List<Cell> cells) {
        Cell result = null;
        double minDistance = Integer.MAX_VALUE;
        for (Cell cell : cells) {
            LatLng target = cell.getLocation();
            double distance = distance(target, loc);
            if (distance < minDistance || minDistance < 0) {
                result = cell;
                minDistance = distance;
            }
        }
        return result;
    }

    public static Cell getClosestNotInUse(LatLng loc, List<Cell> cells) {
        Cell result = null;
        double closestDistance = -1;
        for (Cell cell : cells) {
            if (cell.isInUse()) continue;
            if (!cell.isInRange()) continue;
            double distance = distance(cell.getLocation(), loc);
            if (closestDistance == -1 || distance < closestDistance) {
                result = cell;
                closestDistance = distance;
            }
        }
        if (result != null)
            result.use();
        return result;
    }
}
