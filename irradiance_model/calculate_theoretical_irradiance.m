% Calculates the sun's theoretical irradiance on the position specified by
% latitude and longitude (and other optional inputs) at specified time.
%
% This is based on a calculation from the paper:
% "Integrated simulation of photovoltaic micro-generation and domestic electricity
% demand: a one-minute resolution open-source model" by Ian Richardson and
% Murray Thomson. It has slight modifications: see comments for details.
%
% Sean Leavey
% s.leavey.1@research.gla.ac.uk
% July 2012
%
% Example:
%
%   calculate_theoretical_irradiance(...)
%       Required fields:
%           time,               (MATLAB format)
%           latitude,
%           longitude,
%           panel_slope_angle   (degrees)
%           panel_azimuth_angle (degrees)
%           ground_reflectance
function [total_radiation, altitude] = calculate_theoretical_irradiance(time, latitude, longitude, panel_slope_angle, panel_azimuth_angle, ground_reflectance)

% Constants
solar_constant = 1367; % Watts per square metre

% The specified time in vector form
timevector = datevec(time);

% Work out day of year
first_day_of_year = timevector;
first_day_of_year(:, 2 : end) = 0; % set day and month to 0, leaving only year

day_of_year = time' - datenum(first_day_of_year);

%**************************************************************************
% Calculation of sunlight on ground is mostly taken from Loughborough Excel
% spreadsheet model, with slight modifications. The Loughborough model has
% been taken from this paper: "Integrated simulation of photovoltaic
% micro-generation and domestic electricity demand: a one-minute resolution
% open-source model" - it's the irradiance model, not the whole thing.
%**************************************************************************

% Calculate extraterrestrial radiation
% Calculation based on http://en.wikipedia.org/wiki/Sunlight#cite_ref-5
% Includes correction for distance of sun changing throughout the year
extraterrestrial_radiation = solar_constant .* (1 + 0.033412 .* cosd(2 .* pi .* (day_of_year - 3) ./ 365.25));

% Calculate optical depth of atmostphere
optical_depth = 0.174 + (0.035 .* sind(360 .* (day_of_year - 100) ./ 365));

% B factor
b = 360 .* (day_of_year - 81) ./ 364;

% Equation of time
equation_of_time = 9.87 .* sin(2 .* degtorad(b)) - 7.53 .* cos(degtorad(b)) - 1.5 .* sin(degtorad(b));

% Time correction factor
time_correction_factor = 4 .* longitude + equation_of_time;

% Hours before solar noon
% = 12 - (hour + (minute / 60) + )
hours_before_solar_noon = 12 - (timevector(:, 4) + (timevector(:, 5) ./ 60) + (time_correction_factor ./ 60));

% Hour angle
hour_angle = 15 .* hours_before_solar_noon;

% Declination (radians)
declination = degtorad(23.45 .* sin(degtorad(360 .* (284 + day_of_year) ./ 365.25)));

% Azimuth angle test
if cos(degtorad(hour_angle)) >= (tan(declination) / tan(degtorad(latitude)))
    azimuth_angle_test = false;
else
    azimuth_angle_test = true;
end

% Altitude of sun (radians)
altitude = asin((cos(degtorad(latitude)) .* cos(declination) .* cos(degtorad(hour_angle))) + (sin(degtorad(latitude)) .* sin(declination)));

% Azimuth of sun (radians)
azimuth_of_sun = asin(cos(declination) .* sin(degtorad(hour_angle)) ./ cos(altitude));

% Adjusted azimuth of sun (degrees)
if azimuth_angle_test
    if (radtodeg(azimuth_of_sun) > 0) & (radtodeg(azimuth_of_sun) < 90)
        adjusted_azimuth_of_sun = 180 - radtodeg(azimuth_of_sun);
    else
        if (radtodeg(azimuth_of_sun) > -90) & (radtodeg(azimuth_of_sun) < 0)
            adjusted_azimuth_of_sun = -180 - radtodeg(azimuth_of_sun);
        else
            adjusted_azimuth_of_sun = radtodeg(azimuth_of_sun);
        end
    end
else
    if radtodeg(azimuth_of_sun) > 90
        adjusted_azimuth_of_sun = radtodeg(azimuth_of_sun) - 90;
    else
        if radtodeg(azimuth_of_sun) < -90
            adjusted_azimuth_of_sun = radtodeg(azimuth_of_sun) + 90;
        else
            adjusted_azimuth_of_sun = radtodeg(azimuth_of_sun);
        end
    end
end

% Solar incidence angle on panel (radians)
solar_incidence_angle_on_panel = acos((cos(altitude) .* cos(degtorad(adjusted_azimuth_of_sun) - degtorad(panel_azimuth_angle)) .* sin(degtorad(panel_slope_angle))) + (sin(altitude) .* cos(degtorad(panel_slope_angle))));

% Clear sky beam radiation at horizontal
if altitude > 0
    clear_sky_horizontal_radiation = extraterrestrial_radiation .* exp((0 - optical_depth) ./ sin(altitude));
else
    clear_sky_horizontal_radiation = 0;
end

% Direct beam radiation on panel
if abs(radtodeg(solar_incidence_angle_on_panel)) > 90
    direct_beam_radiation_on_panel = 0;
else
    direct_beam_radiation_on_panel = clear_sky_horizontal_radiation .* cos(solar_incidence_angle_on_panel);
end

% Sky diffuse factor
sky_diffuse_factor = 0.095 + (0.04 .* sin(degtorad(360 .* (day_of_year - 100) ./ 365)));

% Diffuse radiation on panel
diffuse_beam_radiation_on_panel = sky_diffuse_factor .* clear_sky_horizontal_radiation .* ((1 + cos(degtorad(panel_slope_angle))) ./ 2);

% Reflected radiation on panel
reflected_beam_radiation_on_panel = ground_reflectance .* clear_sky_horizontal_radiation .* (sin(altitude) + sky_diffuse_factor) .* ((1 - cos(degtorad(panel_slope_angle))) ./ 2);

% Total radiation on panel
total_radiation = direct_beam_radiation_on_panel + diffuse_beam_radiation_on_panel + reflected_beam_radiation_on_panel;
