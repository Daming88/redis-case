-- 获取锁中的线程标识
local id=redis.call("get",KEYS[1])
-- 比较线程标识和锁中的标识是否一致
if id==ARGV[1] then
    -- 释放锁
    redis.call("del",key)
    return true
end
return 0