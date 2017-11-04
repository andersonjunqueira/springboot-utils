package br.com.iwstech.util.geo;

public class GeoUtils {

    /**
     * Raio médio da terra em quilômetros Ref:
     * http://en.wikipedia.org/wiki/Earth_radius
     */
    public static int EARTH_RADIUS_KM = 6371;

    /**
     * Distância entre dois pontos geográficos. Os valores devem ser informados
     * em graus.
     *
     * @param firstLatitude Latitude do primeiro ponto
     * @param firstLongitude Longitude do primeiro ponto
     * @param secondLatitude Latitude do segundo ponto
     * @param secondLongitude Longitude do segundo ponto
     * @return Distância em quilômetros entre os dois pontos
     */
    public static double geoDistanceInKm(
        double p1Lat, double p1Lon,
        double p2Lat, double p2Lon) {

        // Conversão de graus pra radianos das latitudes
        double firstLatToRad = Math.toRadians(p1Lat);
        double secondLatToRad = Math.toRadians(p2Lat);

        // Diferença das longitudes
        double deltaLongitudeInRad = Math.toRadians(p2Lon - p1Lon);

        // Cálcula da distância entre os pontos
        return Math.acos(Math.cos(firstLatToRad) *
            Math.cos(secondLatToRad) *
            Math.cos(deltaLongitudeInRad) +
            Math.sin(firstLatToRad) *
            Math.sin(secondLatToRad)) *
            EARTH_RADIUS_KM;
    }

    /**
     * Distância entre dois pontos geográficos.
     *
     * @param first Primeira coordenada geográfica
     * @param second Segunda coordenada geográfica
     * @return Distância em quilômetros entre os dois pontos
     */
    public static double geoDistanceInKm(GeoCoordinate p1, GeoCoordinate p2) {
        return geoDistanceInKm(
            p1.getLatitude(), p1.getLongitude(),
            p2.getLatitude(), p2.getLongitude());
    }

}
