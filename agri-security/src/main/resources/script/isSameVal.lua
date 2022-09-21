local key = KEYS[1]
local val = redis.call('get', key)
local val1 = ARGV[1]
local expire = ARGV[2]
if val == nil then
    return -1
end

if val == val1 then
    redis.call('expire', key, expire)
    return 1
else
    return 0
end