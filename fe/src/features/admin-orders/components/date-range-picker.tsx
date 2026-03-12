import { useState } from 'react'
import { CalendarIcon } from 'lucide-react'
import { format, subDays, subMonths, startOfMonth, endOfMonth, startOfYear, endOfYear } from 'date-fns'
import { vi } from 'date-fns/locale'
import { DateRange } from 'react-day-picker'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

type DateRangePickerProps = {
  dateRange: DateRange | undefined
  onDateRangeChange: (range: DateRange | undefined) => void
}

export function DateRangePicker({ dateRange, onDateRangeChange }: DateRangePickerProps) {
  const [isOpen, setIsOpen] = useState(false)

  const presetRanges = [
    {
      label: 'Hôm nay',
      value: 'today',
      range: { from: new Date(), to: new Date() }
    },
    {
      label: '7 ngày qua',
      value: '7days',
      range: { from: subDays(new Date(), 6), to: new Date() }
    },
    {
      label: '30 ngày qua',
      value: '30days',
      range: { from: subDays(new Date(), 29), to: new Date() }
    },
    {
      label: 'Tháng này',
      value: 'thisMonth',
      range: { from: startOfMonth(new Date()), to: endOfMonth(new Date()) }
    },
    {
      label: 'Tháng trước',
      value: 'lastMonth',
      range: { 
        from: startOfMonth(subMonths(new Date(), 1)), 
        to: endOfMonth(subMonths(new Date(), 1)) 
      }
    },
    {
      label: 'Năm này',
      value: 'thisYear',
      range: { from: startOfYear(new Date()), to: endOfYear(new Date()) }
    }
  ]

  const handlePresetSelect = (value: string) => {
    const preset = presetRanges.find(p => p.value === value)
    if (preset) {
      onDateRangeChange(preset.range)
    }
  }

  return (
    <div className="flex items-center gap-2">
      <Select onValueChange={handlePresetSelect}>
        <SelectTrigger className="w-[140px]">
          <SelectValue placeholder="Chọn khoảng" />
        </SelectTrigger>
        <SelectContent>
          {presetRanges.map((preset) => (
            <SelectItem key={preset.value} value={preset.value}>
              {preset.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      <Popover open={isOpen} onOpenChange={setIsOpen}>
        <PopoverTrigger asChild>
          <Button
            id="date"
            variant="outline"
            className={cn(
              'w-[280px] justify-start text-left font-normal',
              !dateRange && 'text-muted-foreground'
            )}
          >
            <CalendarIcon className="mr-2 h-4 w-4" />
            {dateRange?.from ? (
              dateRange.to ? (
                <>
                  {format(dateRange.from, 'dd/MM/yyyy', { locale: vi })} -{' '}
                  {format(dateRange.to, 'dd/MM/yyyy', { locale: vi })}
                </>
              ) : (
                format(dateRange.from, 'dd/MM/yyyy', { locale: vi })
              )
            ) : (
              <span>Chọn khoảng thời gian</span>
            )}
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-auto p-0" align="start">
          <Calendar
            initialFocus
            mode="range"
            defaultMonth={dateRange?.from}
            selected={dateRange}
            onSelect={onDateRangeChange}
            numberOfMonths={2}
            locale={vi}
          />
        </PopoverContent>
      </Popover>
    </div>
  )
}