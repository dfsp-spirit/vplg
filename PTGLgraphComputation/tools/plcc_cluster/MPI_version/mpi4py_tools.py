def data_divide(_data, n):
   result=[]
   part_size=int(len(_data)/n)
   if len(_data)%n != 0:
      part_size+=1
   for i in range(1,n+1):
      min=(i-1)*part_size
      max=(i)*part_size
      if max>len(_data):
         max=len(_data)
      
      result.append(_data[min:max])
   
   return result
   
def data_merge(_data):
   result=[]
   for part in _data:
      for entry in part:
         result.append(entry)
   return result