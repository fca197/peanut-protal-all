
SELECT *
FROM `store_business_district`
WHERE
    -- 计算当前点与记录点的球面距离（米）
    ST_Distance_Sphere(
            POINT(121.464102, 31.23132), -- 表中存储的地理点
            POINT(121.464102, 31.23132)-- 当前经纬度转换为地理点
    )    <= business_district_radius; -- 距离小于等于记录的半径