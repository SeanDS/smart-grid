Irradiance Model
================

A MATLAB script to calculate solar irradiance.

This script calculates the level of solar irradiance (watts per square metre) incident on an area of ground of a given angle, at a given time of year, at a given position on Earth.

The calculation is mostly based on a solar model developed by researchers in Loughborough University, but their source code was provided in the form of a VBA script embedded in an Excel document, so this is a slightly more accessible version. See the script comments for details of where to find the original. All credit to Ian Richardson and Murray Thomson.

In MATLAB, just run the script like this:
 
[total_radiation, altitude] = calculate_theoretical_irradiance(time, latitude, longitude, panel_slope_angle, panel_azimuth_angle, ground_reflectance)
 
Notes
-----
 
 * The time must be a single date or vector of dates, in MATLAB date format (make MATLAB date strings by using the command 'datevec').
 * All angles are in degrees.
 * Ground reflectance is a value between 0 and 1. Solar panels usually reflect something like 4% of the incident light, so that would be 0.04.
 
Sean Leavey
July 2012
https://github.com/SeanDS/