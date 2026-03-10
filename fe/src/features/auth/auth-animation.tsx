import Rive from '@rive-app/react-canvas'
import bookAnim from '@/assets/book-animation.riv'

export function AuthAnimation() {
    return (
        <div className='flex h-full w-full items-center justify-center bg-[#f7f3ed] overflow-hidden'>
            <div className='flex h-full w-full items-center justify-center scale-[1.6]'>
                <Rive
                    src={bookAnim}
                    stateMachines='State Machine 1'
                    className='h-[600px] w-[600px]'
                />
            </div>
        </div>

    )
}

