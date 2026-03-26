import { useState } from 'react'
import { Check, ChevronsUpDown } from 'lucide-react'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { useVietnamAddress, type Province, type District, type Ward } from '@/hooks/use-vietnam-address'

interface AddressSelectorProps {
  onProvinceChange: (province: Province | null) => void
  onDistrictChange: (district: District | null) => void
  onWardChange: (ward: Ward | null) => void
  selectedProvince?: Province | null
  selectedDistrict?: District | null
  selectedWard?: Ward | null
  // Default values from database
  defaultCity?: string | null
  defaultDistrict?: string | null
  defaultWard?: string | null
}

export function AddressSelector({
  onProvinceChange,
  onDistrictChange,
  onWardChange,
  selectedProvince,
  selectedDistrict,
  selectedWard,
  defaultCity,
  defaultDistrict,
  defaultWard,
}: AddressSelectorProps) {
  const { provinces, districts, wards, loading, fetchDistricts, fetchWards } = useVietnamAddress()
  const [openProvince, setOpenProvince] = useState(false)
  const [openDistrict, setOpenDistrict] = useState(false)
  const [openWard, setOpenWard] = useState(false)

  const handleProvinceSelect = async (province: Province) => {
    try {
      onProvinceChange(province)
      onDistrictChange(null) // Reset district
      onWardChange(null) // Reset ward
      setOpenProvince(false)
      await fetchDistricts(province.code)
    } catch (error) {
      console.error('Error fetching districts:', error)
    }
  }

  const handleDistrictSelect = async (district: District) => {
    try {
      onDistrictChange(district)
      onWardChange(null) // Reset ward
      setOpenDistrict(false)
      await fetchWards(district.code)
    } catch (error) {
      
    }
  }

  // Get display text for buttons
  const getProvinceDisplayText = () => {
    if (selectedProvince) return selectedProvince.name
    if (defaultCity) return defaultCity
    return 'Chọn tỉnh/thành phố'
  }

  const getDistrictDisplayText = () => {
    if (selectedDistrict) return selectedDistrict.name
    if (defaultDistrict) return defaultDistrict
    return 'Chọn quận/huyện'
  }

  const getWardDisplayText = () => {
    if (selectedWard) return selectedWard.name
    if (defaultWard) return defaultWard
    return 'Chọn phường/xã'
  }

  const handleWardSelect = (ward: Ward) => {
    onWardChange(ward)
    setOpenWard(false)
  }

  return (
    <div className='grid grid-cols-1 md:grid-cols-3 gap-4'>
      {/* Province Selector */}
      <div className='space-y-2'>
        <label className='text-sm font-medium'>Tỉnh/Thành phố</label>
        <Popover open={openProvince} onOpenChange={setOpenProvince}>
          <PopoverTrigger asChild>
            <Button
              variant='outline'
              role='combobox'
              aria-expanded={openProvince}
              className='w-full justify-between'
              disabled={loading.provinces}
            >
              {loading.provinces ? 'Đang tải...' : getProvinceDisplayText()}
              <ChevronsUpDown className='ml-2 h-4 w-4 shrink-0 opacity-50' />
            </Button>
          </PopoverTrigger>
          <PopoverContent className='w-[300px] p-0' align="start">
            <Command>
              <CommandInput placeholder='Tìm tỉnh/thành phố...' />
              <CommandEmpty>Không tìm thấy tỉnh/thành phố.</CommandEmpty>
              <CommandGroup>
                <CommandList>
                  {provinces.map((province) => (
                    <CommandItem
                      key={province.code}
                      value={`${province.code}-${province.name}`}
                      onSelect={() => handleProvinceSelect(province)}
                    >
                      <Check
                        className={cn(
                          'mr-2 h-4 w-4',
                          selectedProvince?.code === province.code ? 'opacity-100' : 'opacity-0'
                        )}
                      />
                      {province.name}
                    </CommandItem>
                  ))}
                </CommandList>
              </CommandGroup>
            </Command>
          </PopoverContent>
        </Popover>
      </div>

      {/* District Selector */}
      <div className='space-y-2'>
        <label className='text-sm font-medium'>Quận/Huyện</label>
        <Popover open={openDistrict} onOpenChange={setOpenDistrict}>
          <PopoverTrigger asChild>
            <Button
              variant='outline'
              role='combobox'
              aria-expanded={openDistrict}
              className='w-full justify-between'
              disabled={(!selectedProvince && !defaultCity) || loading.districts}
            >
              {loading.districts ? 'Đang tải...' : getDistrictDisplayText()}
              <ChevronsUpDown className='ml-2 h-4 w-4 shrink-0 opacity-50' />
            </Button>
          </PopoverTrigger>
          <PopoverContent className='w-[300px] p-0' align="start">
            <Command>
              <CommandInput placeholder='Tìm quận/huyện...' />
              <CommandEmpty>Không tìm thấy quận/huyện.</CommandEmpty>
              <CommandGroup>
                <CommandList>
                  {districts.map((district) => (
                    <CommandItem
                      key={district.code}
                      value={`${district.code}-${district.name}`}
                      onSelect={() => handleDistrictSelect(district)}
                    >
                      <Check
                        className={cn(
                          'mr-2 h-4 w-4',
                          selectedDistrict?.code === district.code ? 'opacity-100' : 'opacity-0'
                        )}
                      />
                      {district.name}
                    </CommandItem>
                  ))}
                </CommandList>
              </CommandGroup>
            </Command>
          </PopoverContent>
        </Popover>
      </div>

      {/* Ward Selector */}
      <div className='space-y-2'>
        <label className='text-sm font-medium'>Phường/Xã</label>
        <Popover open={openWard} onOpenChange={setOpenWard}>
          <PopoverTrigger asChild>
            <Button
              variant='outline'
              role='combobox'
              aria-expanded={openWard}
              className='w-full justify-between'
              disabled={(!selectedDistrict && !defaultDistrict) || loading.wards}
            >
              {loading.wards ? 'Đang tải...' : getWardDisplayText()}
              <ChevronsUpDown className='ml-2 h-4 w-4 shrink-0 opacity-50' />
            </Button>
          </PopoverTrigger>
          <PopoverContent className='w-[300px] p-0' align="start">
            <Command>
              <CommandInput placeholder='Tìm phường/xã...' />
              <CommandEmpty>Không tìm thấy phường/xã.</CommandEmpty>
              <CommandGroup>
                <CommandList>
                  {wards.map((ward) => (
                    <CommandItem
                      key={ward.code}
                      value={`${ward.code}-${ward.name}`}
                      onSelect={() => handleWardSelect(ward)}
                    >
                      <Check
                        className={cn(
                          'mr-2 h-4 w-4',
                          selectedWard?.code === ward.code ? 'opacity-100' : 'opacity-0'
                        )}
                      />
                      {ward.name}
                    </CommandItem>
                  ))}
                </CommandList>
              </CommandGroup>
            </Command>
          </PopoverContent>
        </Popover>
      </div>
    </div>
  )
}