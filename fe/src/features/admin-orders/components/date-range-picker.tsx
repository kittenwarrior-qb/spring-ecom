import { useState } from 'react'
import { CalendarIcon } from 'lucide-react'
import { format, subDays, subMonths, startOfMonth, endOfMonth, startOfYear, endOfYear, eachMonthOfInterval, eachYearOfInterval } from 'date-fns'
import { vi } from 'date-fns/locale'
import type { DateRange } from 'react-day-picker'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'

type DateRangePickerProps = {
  dateRange: DateRange | undefined
  onDateRangeChange: (range: DateRange | undefined) => void
}

export function DateRangePicker({ dateRange, onDateRangeChange }: DateRangePickerProps) {
  const [isOpen, setIsOpen] = useState(false)
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())

  // Generate years from 2020 to current year
  const years = eachYearOfInterval({
    start: new Date(2020, 0, 1),
    end: new Date()
  }).map(date => date.getFullYear()).reverse()

  // Generate months for selected year
  const months = eachMonthOfInterval({
    start: new Date(selectedYear, 0, 1),
    end: new Date(selectedYear, 11, 31)
  })

  const presetRanges = [
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
      label: 'Năm nay',
      value: 'thisYear',
      range: { from: startOfYear(new Date()), to: endOfYear(new Date()) }
    },
  ]

  const handlePresetSelect = (value: string) => {
    const preset = presetRanges.find(p => p.value === value)
    if (preset) {
      onDateRangeChange(preset.range)
    }
  }

  const handleMonthSelect = (monthIndex: number) => {
    const month = new Date(selectedYear, monthIndex, 1)
    onDateRangeChange({
      from: startOfMonth(month),
      to: endOfMonth(month)
    })
    setIsOpen(false)
  }

  const handleYearSelect = (year: number) => {
    onDateRangeChange({
      from: startOfYear(new Date(year, 0, 1)),
      to: endOfYear(new Date(year, 0, 1))
    })
    setIsOpen(false)
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
          <Tabs defaultValue="calendar" className="w-[500px]">
            <TabsList className="w-full grid grid-cols-3">
              <TabsTrigger value="calendar">Lịch</TabsTrigger>
              <TabsTrigger value="month">Theo tháng</TabsTrigger>
              <TabsTrigger value="year">Theo năm</TabsTrigger>
            </TabsList>
            
            <TabsContent value="calendar" className="p-0">
              <Calendar
                initialFocus
                mode="range"
                defaultMonth={dateRange?.from}
                selected={dateRange}
                onSelect={onDateRangeChange}
                numberOfMonths={2}
                locale={vi}
              />
            </TabsContent>
            
            <TabsContent value="month" className="p-4">
              <div className="space-y-4">
                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium">Chọn năm:</span>
                  <Select value={selectedYear.toString()} onValueChange={(v) => setSelectedYear(parseInt(v))}>
                    <SelectTrigger className="w-[120px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {years.map(year => (
                        <SelectItem key={year} value={year.toString()}>{year}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="grid grid-cols-4 gap-2">
                  {months.map((month, index) => (
                    <Button
                      key={index}
                      variant="outline"
                      size="sm"
                      onClick={() => handleMonthSelect(index)}
                      className="w-full"
                    >
                      {format(month, 'MMMM', { locale: vi })}
                    </Button>
                  ))}
                </div>
              </div>
            </TabsContent>
            
            <TabsContent value="year" className="p-4">
              <div className="grid grid-cols-4 gap-2">
                {years.map(year => (
                  <Button
                    key={year}
                    variant="outline"
                    size="sm"
                    onClick={() => handleYearSelect(year)}
                    className="w-full"
                  >
                    {year}
                  </Button>
                ))}
              </div>
            </TabsContent>
          </Tabs>
        </PopoverContent>
      </Popover>
    </div>
  )
}