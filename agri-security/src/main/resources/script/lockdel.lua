local key = KEYS[1]
local val = ARGV[1]
local val1 = redis.call('get', key)

if val1 == val then
    return redis.call('del', key)
else
    return 0
end
